package com.piresdio.kafkaexample.consumers

import com.piresdio.kafkaexample.kafkaexception.MethodNotAllowedException
import com.piresdio.kafkaexample.serializers.SnackDeserializer
import com.piresdio.kafkaexample.messages.SnackMessage
import com.piresdio.kafkaexample.repository.SnackRepository
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMethod
import java.time.Duration
import java.util.*

class SnackConsumer(brokers: String, @Autowired private val snackRepository: SnackRepository? = null) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val consumer = createConsumer(brokers)

    private fun createConsumer(brokers: String): Consumer<String, SnackMessage> {
        val props = Properties()
        props["bootstrap.servers"] = brokers
        props["group.id"] = "snack-consumer"
        props["key.deserializer"] = StringDeserializer::class.java
        props["value.deserializer"] = SnackDeserializer::class.java
        return KafkaConsumer(props)
    }

    fun consume(topics: List<String>) {
        consumer.subscribe(topics)
        logger.info("Subscribed to $topics, consuming data...")

        while(true) {
            val records = consumer.poll(Duration.ofMillis(1000))
            logger.info("Received ${records.count()} records...")

            records.iterator().forEach {
                val snackMessage = it.value()
                logger.info("Processing record $snackMessage")

                when (snackMessage.method) {
                    in listOf(RequestMethod.POST, RequestMethod.PUT) -> {
                        snackRepository!!.save(snackMessage.snack)
                    }
                    RequestMethod.DELETE -> {
                        snackRepository!!
                                .findById(snackMessage.snack.id!!)
                                .map { snack ->
                                    snackRepository.delete(snack)
                                }
                    }
                    else -> {
                        throw MethodNotAllowedException("Method ${snackMessage.method.name} sent in the SnackMessage not allowed!")
                    }
                }
            }
        }
    }
}