package com.example.ec.order.app.domain.models.order

import com.example.ec.eventsourcing.core.CommandProcessingAggregate
import com.example.ec.eventsourcing.core.event.Event
import com.example.ec.order.api.OrderState
import com.example.ec.shared.money.models.money.Money

class Order : CommandProcessingAggregate<Order, OrderCommand>() {
    lateinit var accountId: String
    var total: Money = Money.zero()
    lateinit var items: OrderItems
    lateinit var orderState: OrderState

    fun process(command: CreateOrderCommand): List<Event> {
        val total = command.orderItems.total()
        val event = OrderCreatedEvent(command.accountId, total, command.orderItems)
        return listOf(event)
    }

    fun apply(event: OrderCreatedEvent) {
        accountId = event.accountId
        total = event.total
        items = event.orderItems
        orderState = OrderState.APPROVAL_PENDING
    }

    fun process(command: ApproveOrderCommand): List<Event> {
        return listOf(OrderApprovedEvent())
    }

    fun apply(event: OrderApprovedEvent) {
        this.orderState = OrderState.APPROVED
    }

    fun process(command: RejectOrderCommand): List<Event> {
        return listOf(OrderRejectedEvent())
    }

    fun apply(event: OrderRejectedEvent) {
        this.orderState = OrderState.APPROVAL_REJECTED
    }
}