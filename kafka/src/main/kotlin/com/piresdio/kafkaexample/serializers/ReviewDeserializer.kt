package com.piresdio.kafkaexample.serializers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.piresdio.kafkaexample.messages.ReviewMessage
import org.apache.kafka.common.serialization.Deserializer

class ReviewDeserializer: Deserializer<ReviewMessage> {
    override fun close() {}
    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {}

    override fun deserialize(topic: String?, data: ByteArray?): ReviewMessage? {
        if (data == null) return null
        return jacksonObjectMapper().readValue(data, ReviewMessage::class.java)
    }
}