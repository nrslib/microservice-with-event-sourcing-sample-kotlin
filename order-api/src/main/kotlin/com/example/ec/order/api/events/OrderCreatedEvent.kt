package com.example.ec.order.api.events

import com.example.ec.shared.money.models.money.Money

class OrderCreatedEvent {
    lateinit var id: String
    lateinit var accountId: String
    lateinit var items: List<OrderItem>
    lateinit var total: Money

    constructor() {}

    constructor(id: String, accountId: String, items: List<OrderItem>, total: Money) {
        this.id = id
        this.accountId = accountId
        this.items = items
        this.total = total
    }
}