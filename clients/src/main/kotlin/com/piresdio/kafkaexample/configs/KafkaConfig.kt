package com.piresdio.kafkaexample.configs

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
open class KafkaConfig {
    @Value("\${brokers}")
    lateinit var brokers: String

    @Value("\${snacks_topic}")
    lateinit var snacksTopic: String

    @Value("\${reviews_topic}")
    lateinit var reviewsTopic: String
}