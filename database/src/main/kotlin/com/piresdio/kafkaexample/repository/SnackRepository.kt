package com.piresdio.kafkaexample.repository

import com.piresdio.kafkaexample.model.Snack
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SnackRepository: JpaRepository<Snack, String> {
    fun existsByName(snackName: String): Boolean
}