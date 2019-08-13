package com.piresdio.kafkaexample.controller

import com.piresdio.kafkaexample.configs.KafkaConfig
import com.piresdio.kafkaexample.consumers.ReviewConsumer
import com.piresdio.kafkaexample.exception.ResourceNotFoundException
import com.piresdio.kafkaexample.messages.ReviewMessage
import com.piresdio.kafkaexample.model.Review
import com.piresdio.kafkaexample.producers.ReviewProducer
import com.piresdio.kafkaexample.repository.ReviewRepository
import com.piresdio.kafkaexample.repository.SnackRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid

@RestController
class ReviewController(@Autowired var reviewRepository: ReviewRepository, @Autowired var snackRepository: SnackRepository, @Autowired final val kafkaConfig: KafkaConfig) {
    private val reviewsTopic = kafkaConfig.reviewsTopic
    private val reviewProducer = ReviewProducer(kafkaConfig.brokers)

    init {
        GlobalScope
                .launch {
                    ReviewConsumer(
                            kafkaConfig.brokers,
                            snackRepository = snackRepository,
                            reviewRepository = reviewRepository
                    ).consume(listOf(reviewsTopic))
                }
    }

    @GetMapping("/reviews/{snackId}")
    fun reviewsBySnackId(@PathVariable snackId: String): List<Review> {
        return reviewRepository.findBySnackId(snackId)
    }

    @GetMapping("/reviews")
    fun reviewsBySnackId(): List<Review> {
        return reviewRepository.findAll()
    }

    @PostMapping("/reviews/{snackId}")
    fun createReview(@PathVariable snackId: String, @Valid @RequestBody review: Review): String {
        if (!snackRepository.existsById(snackId)) {
            throw ResourceNotFoundException("Snack with ID $snackId not found!")
        }

        val reviewId = UUID.randomUUID().toString()
        reviewProducer.produce(
                reviewsTopic,
                ReviewMessage(
                        review,
                        reviewId,
                        snackId,
                        RequestMethod.POST
                )
        )

        return reviewId
    }

    @PutMapping("/reviews/{reviewId}")
    fun updateReview(@PathVariable reviewId: String, @Valid @RequestBody review: Review): String {
        if (!reviewRepository.existsById(reviewId)) {
            throw ResourceNotFoundException("Review with ID $reviewId not found!")
        }

        reviewProducer.produce(
                reviewsTopic,
                ReviewMessage(
                        review = review,
                        reviewId = reviewId,
                        method = RequestMethod.PUT
                )
        )

        return reviewId
    }

    @DeleteMapping("/reviews/{reviewId}")
    fun deleteReview(@PathVariable reviewId: String): ResponseEntity<Unit> {
        if (!reviewRepository.existsById(reviewId)) {
            throw ResourceNotFoundException("Review with ID $reviewId not found!")
        }

        reviewProducer.produce(
                reviewsTopic,
                ReviewMessage(
                        reviewId = reviewId,
                        method = RequestMethod.DELETE
                )
        )
        return ResponseEntity.ok().build()
    }

}