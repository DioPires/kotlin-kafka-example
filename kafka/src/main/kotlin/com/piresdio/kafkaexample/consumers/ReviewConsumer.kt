package com.piresdio.kafkaexample.consumers

import com.piresdio.kafkaexample.kafkaexception.MethodNotAllowedException
import com.piresdio.kafkaexample.messages.ReviewMessage
import com.piresdio.kafkaexample.repository.ReviewRepository
import com.piresdio.kafkaexample.repository.SnackRepository
import com.piresdio.kafkaexample.serializers.ReviewDeserializer
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMethod
import java.time.Duration
import java.util.*

class ReviewConsumer (
        brokers: String,
        @Autowired private val snackRepository: SnackRepository? = null,
        @Autowired private val reviewRepository: ReviewRepository? = null
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val consumer = createConsumer(brokers)

    private fun createConsumer(brokers: String): Consumer<String, ReviewMessage> {
        val props = Properties()
        props["bootstrap.servers"] = brokers
        props["group.id"] = "snack-consumer"
        props["key.deserializer"] = StringDeserializer::class.java
        props["value.deserializer"] = ReviewDeserializer::class.java
        return KafkaConsumer(props)
    }

    fun consume(topics: List<String>) {
        consumer.subscribe(topics)
        logger.info("Subscribed to $topics, consuming data...")

        while(true) {
            val records = consumer.poll(Duration.ofMillis(1000))
            logger.info("Received ${records.count()} records...")

            records.iterator().forEach {
                val reviewMessage = it.value()
                logger.info("Processing record $reviewMessage")

                when (reviewMessage.method) {
                    RequestMethod.POST -> {
                        snackRepository!!
                                .findById(reviewMessage.snackId!!)
                                .map { snack ->
                                    reviewMessage.review!!.id = reviewMessage.reviewId
                                    reviewMessage.review.snack = snack
                                    reviewRepository!!.save(reviewMessage.review)
                                }
                    }
                    RequestMethod.PUT -> {
                        reviewRepository!!
                                .findById(reviewMessage.reviewId)
                                .map { reviewOld ->
                                    reviewOld.rating = reviewMessage.review!!.rating
                                    reviewOld.text = reviewMessage.review.text
                                    reviewOld.title = reviewMessage.review.title
                                    reviewRepository.save(reviewOld)
                                }
                    }
                    RequestMethod.DELETE -> {
                        reviewRepository!!
                                .findById(reviewMessage.reviewId)
                                .map { review ->
                                    reviewRepository.delete(review)
                                }
                    }
                    else -> {
                        throw MethodNotAllowedException("Method ${reviewMessage.method.name} sent in the ReviewMessage not allowed!")
                    }
                }
            }
        }
    }
}