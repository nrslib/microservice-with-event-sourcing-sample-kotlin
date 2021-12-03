package com.example.ec.order.app.domain.models.order

import com.example.ec.eventsourcing.core.event.Event
import com.example.ec.shared.money.models.money.Money

class OrderCreatedEvent(val accountId: String, val total: Money, val orderItems: OrderItems) : Event