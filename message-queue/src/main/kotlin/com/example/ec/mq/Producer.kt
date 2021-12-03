package com.example.ec.mq

interface Producer {
    fun produce(message: Any)
}