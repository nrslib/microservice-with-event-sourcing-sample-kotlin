package com.example.ec.order.app.handler

import com.example.ec.applicationsupportstack.microservicesupport.saga.SagaManager
import com.example.ec.order.api.order.events.OrderCreatedEvent
import org.springframework.context.event.EventListener

class OrderEventHandler(private val sagaManager: SagaManager) {
    @EventListener
    fun handle(orderCreatedEvent: OrderCreatedEvent) {
        sagaManager.start(orderCreatedEvent)
    }
}