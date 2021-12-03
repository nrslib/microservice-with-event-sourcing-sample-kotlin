package com.example.ec.order.app.domain.models.order

class CreateOrderCommand(
    val accountId: String,
    val orderItems: OrderItems
) : OrderCommand