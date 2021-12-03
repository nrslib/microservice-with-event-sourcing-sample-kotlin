package com.example.ec.billing.configs

import com.example.ec.billing.messaging.consumers.BillingMessageListener
import com.example.ec.jmsmessaging.JmsProducer
import com.example.ec.mq.Producer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.jms.core.JmsTemplate
import org.springframework.jms.support.converter.MappingJackson2MessageConverter
import org.springframework.jms.support.converter.MessageConverter
import org.springframework.jms.support.converter.MessageType

@Configuration
@Profile("azure")
class MessagingConfig {
    @Bean
    fun jsonMessageConverter(): MessageConverter {
        val converter = MappingJackson2MessageConverter()
        converter.setTargetType(MessageType.TEXT)
        converter.setTypeIdPropertyName("_type")
        return converter
    }

    @Bean
    fun billingMessageListener(@Qualifier("createOrderSagaReplyProducer") createOrderSagaReplyProducer: Producer): BillingMessageListener {
        return BillingMessageListener(createOrderSagaReplyProducer)
    }

    @Bean("createOrderSagaReplyProducer")
    fun createOrderSagaReplyProducer(jmsTemplate: JmsTemplate): Producer {
        return JmsProducer(jmsTemplate, "create-order-saga-reply-queue")
    }
}