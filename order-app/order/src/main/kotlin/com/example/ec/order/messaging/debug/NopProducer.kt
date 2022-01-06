package com.example.ec.order.messaging.debug

import com.example.ec.mq.Producer

class NopProducer : Producer {
    override fun produce(message: Any) {
    }
}