package com.example.ec.order.app.domain.models.order

import com.example.ec.order.api.order.OrderItems

class CreateOrderCommand(
    val id: String,
    val accountId: String,
    val orderItems: OrderItems
) : OrderCommand