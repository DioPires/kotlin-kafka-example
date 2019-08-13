package com.piresdio.kafkaexample.serializers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.piresdio.kafkaexample.messages.ReviewMessage
import org.apache.kafka.common.serialization.Serializer

class ReviewSerializer: Serializer<ReviewMessage> {
    override fun close() {}
    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {}

    override fun serialize(topic: String?, data: ReviewMessage?): ByteArray? {
        if (data == null) return null
        return jacksonObjectMapper().writeValueAsBytes(data)
    }
}