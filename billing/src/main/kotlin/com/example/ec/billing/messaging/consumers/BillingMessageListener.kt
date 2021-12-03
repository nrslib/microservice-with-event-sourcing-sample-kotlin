package com.example.ec.billing.messaging.consumers

import com.example.ec.billing.api.AuthorizeCommand
import com.example.ec.billing.api.AuthorizeSuccessReply
import com.example.ec.mq.Producer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jms.annotation.JmsListener

class BillingMessageListener(@Qualifier("createOrderSagaReplyProducer") private val createOrderSagaReplyProducer: Producer) {
    @JmsListener(destination = "billing-command-queue")
    fun handle(command: AuthorizeCommand) {
        createOrderSagaReplyProducer.produce(AuthorizeSuccessReply(command.orderId))
    }
}