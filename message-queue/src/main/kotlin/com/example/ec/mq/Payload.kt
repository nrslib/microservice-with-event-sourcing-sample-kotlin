package com.example.ec.mq

import com.google.gson.Gson
import java.io.Serializable

data class Payload(
    val type: String,
    val content: String
) : Serializable { // For Azure Service Bus | RabbitMQ
    companion object {
        fun apply(message: Any): Payload {
            val messageJson = Gson().toJson(message)
            return Payload(message.javaClass.typeName, messageJson)
        }

        fun unapply(json: String): Any {
            val payload = Gson().fromJson(json, Payload::class.java)
            val clazz = Class.forName(payload.type)
            val command = Gson().fromJson(payload.content, clazz)
            return command
        }
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }

    fun toObject(): Any {
        val clazz = Class.forName(type)
        return Gson().fromJson(content, clazz)
    }

    constructor() : this("", "") // For AWS SQS
}