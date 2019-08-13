package com.piresdio.kafkaexample

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class KafkaExampleApplication

fun main(args: Array<String>) {
	runApplication<KafkaExampleApplication>(*args)
}
