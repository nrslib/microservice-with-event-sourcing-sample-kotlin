package com.example.ec.jmsmessaging

import com.example.ec.mq.Producer
import org.springframework.jms.core.JmsTemplate

class JmsProducer(
    private val jmsTemplate: JmsTemplate,
    private val destination: String
) : Producer {
    override fun produce(message: Any) {
        jmsTemplate.convertAndSend(destination, message)
    }
}