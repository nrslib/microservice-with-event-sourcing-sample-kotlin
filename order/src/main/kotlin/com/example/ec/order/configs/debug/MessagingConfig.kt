package com.example.ec.order.configs.debug

import com.example.ec.applicationsupportstack.microservicesupport.saga.SagaManager
import com.example.ec.jmsmessaging.JmsProducer
import com.example.ec.mq.Producer
import com.example.ec.order.messaging.consumers.CreateOrderSagaReplyConsumer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.jms.core.JmsTemplate
import org.springframework.jms.support.converter.MappingJackson2MessageConverter
import org.springframework.jms.support.converter.MessageConverter
import org.springframework.jms.support.converter.MessageType

@Configuration
@Profile("debug")
class MessagingConfig {
    @Bean
    fun jsonMessageConverter(): MessageConverter {
        val converter = MappingJackson2MessageConverter()
        converter.setTargetType(MessageType.TEXT)
        converter.setTypeIdPropertyName("_type")
        return converter
    }

    @Bean("billingCommandProducer")
    fun billingCommandProducer(jmsTemplate: JmsTemplate): Producer {
        return JmsProducer(jmsTemplate, "billing-command-queue")
    }

    @Bean
    fun createOrderSagaReplyConsumer(sagaManager: SagaManager): CreateOrderSagaReplyConsumer {
        return CreateOrderSagaReplyConsumer(sagaManager)
    }
}