package com.piresdio.kafkaexample.serializers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.piresdio.kafkaexample.messages.SnackMessage
import org.apache.kafka.common.serialization.Serializer

class SnackSerializer: Serializer<SnackMessage> {
    override fun close() {}
    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {}

    override fun serialize(topic: String?, data: SnackMessage?): ByteArray? {
        if (data == null) return null
        return jacksonObjectMapper().writeValueAsBytes(data)
    }
}