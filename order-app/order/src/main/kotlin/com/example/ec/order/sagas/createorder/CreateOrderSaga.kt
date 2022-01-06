package com.example.ec.order.sagas.createorder

import com.example.ec.applicationsupportstack.microservicesupport.saga.Saga
import com.example.ec.billing.api.AuthorizeCommand
import com.example.ec.billing.api.AuthorizeFailReply
import com.example.ec.billing.api.AuthorizeSuccessReply
import com.example.ec.mq.Producer
import com.example.ec.order.api.order.events.OrderCreatedEvent
import com.example.ec.order.app.application.order.OrderApplicationService
import org.springframework.beans.factory.annotation.Qualifier

class CreateOrderSaga(
    @Qualifier("billingCommandProducer") private val producer: Producer,
    private var orderApplicationService: OrderApplicationService
) : Saga {
    fun handle(e: OrderCreatedEvent) {
        val command = AuthorizeCommand(e.id, e.accountId, e.total)
        producer.produce(command)
    }

    fun handle(reply: AuthorizeSuccessReply) {
        orderApplicationService.approveOrder(reply.orderId)
    }

    fun handle(reply: AuthorizeFailReply) {
        orderApplicationService.rejectOrder(reply.orderId)
    }
}