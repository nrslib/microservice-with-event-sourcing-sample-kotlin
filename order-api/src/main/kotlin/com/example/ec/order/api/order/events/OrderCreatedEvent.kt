package com.example.ec.order.api.order.events

import com.example.ec.eventsourcing.core.event.Event
import com.example.ec.order.api.order.OrderItems
import com.example.ec.shared.money.models.money.Money

class OrderCreatedEvent(val id: String, val accountId: String, val total: Money, val orderItems: OrderItems) : Event