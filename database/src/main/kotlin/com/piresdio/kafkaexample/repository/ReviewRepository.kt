package com.piresdio.kafkaexample.repository

import com.piresdio.kafkaexample.model.Review
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReviewRepository: JpaRepository<Review, String> {
    fun findBySnackId(snackId: String): List<Review>
}