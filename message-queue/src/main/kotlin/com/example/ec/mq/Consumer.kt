package com.example.ec.mq

interface Consumer {
    fun consume(message: String)
}