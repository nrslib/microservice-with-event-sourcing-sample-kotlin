package com.example.ec.order.api.events

class OrderItem {
    lateinit var itemId: String
    var quantity: Int = 0

    constructor() {
    }

    constructor(itemId: String, quantity: Int) {
        this.itemId = itemId
        this.quantity = quantity
    }
}