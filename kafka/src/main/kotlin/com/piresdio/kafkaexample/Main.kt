package com.piresdio.kafkaexample

import com.piresdio.kafkaexample.consumers.ReviewConsumer
import com.piresdio.kafkaexample.consumers.SnackConsumer

fun main(args: Array<String>) {
    val snackConsumer = SnackConsumer("localhost:9092")
    snackConsumer.consume(listOf("snacks"))

    val reviewConsumer = ReviewConsumer("localhost:9092")
    reviewConsumer.consume(listOf("reviews"))
}