package com.example.ec.order.messaging.consumers

import com.example.ec.applicationsupportstack.microservicesupport.saga.SagaManager
import com.example.ec.billing.api.AuthorizeFailReply
import com.example.ec.billing.api.AuthorizeSuccessReply
import org.springframework.jms.annotation.JmsListener

class CreateOrderSagaReplyConsumer(private val sagaManager: SagaManager) {
    @JmsListener(destination = "create-order-saga-reply-queue")
    fun handle(reply: AuthorizeSuccessReply) {
        sagaManager.start(reply)
    }

    @JmsListener(destination = "create-order-saga-reply-queue")
    fun handle(reply: AuthorizeFailReply) {
        sagaManager.start(reply)
    }
}