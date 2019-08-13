package com.piresdio.kafkaexample.producers

import com.piresdio.kafkaexample.serializers.ReviewSerializer
import com.piresdio.kafkaexample.messages.ReviewMessage
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.LoggerFactory
import java.util.*

class ReviewProducer(brokers: String) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val producer = createProducer(brokers)

    private fun createProducer(brokers: String): Producer<String, ReviewMessage> {
        val props = Properties()
        props["bootstrap.servers"] = brokers
        props["key.serializer"] = StringSerializer::class.java
        props["value.serializer"] = ReviewSerializer::class.java
        return KafkaProducer(props)
    }

    fun produce(topic: String, data: ReviewMessage) {
        logger.info("Producing a message with data $data")
        val futureResult = producer.send(ProducerRecord(topic, data))

        // Wait for acknowledgment
        futureResult.get()
        logger.info("Sent message $data to $topic")
    }
}