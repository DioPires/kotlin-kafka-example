package com.piresdio.kafkaexample.serializers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.piresdio.kafkaexample.messages.SnackMessage
import org.apache.kafka.common.serialization.Deserializer

class SnackDeserializer: Deserializer<SnackMessage> {
    override fun close() {}
    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {}

    override fun deserialize(topic: String?, data: ByteArray?): SnackMessage? {
        if (data == null) return null
        return jacksonObjectMapper().readValue(data, SnackMessage::class.java)
    }
}