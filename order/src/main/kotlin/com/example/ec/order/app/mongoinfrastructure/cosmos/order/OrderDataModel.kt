package com.example.ec.order.app.mongoinfrastructure.cosmos.order

import com.azure.spring.data.cosmos.core.mapping.Container
import com.azure.spring.data.cosmos.core.mapping.GeneratedValue
import com.example.ec.order.api.OrderState
import org.springframework.data.annotation.Id

@Container(containerName = "order_orders")
class OrderDataModel {
    @GeneratedValue
    @Id
    lateinit var id: String
    lateinit var state: OrderState
    lateinit var items: List<OrderItemDataModel>

    constructor()

    constructor(
        id: String,
        state: OrderState,
        items: List<OrderItemDataModel>
    ) {
        this.id = id
        this.state = state
        this.items = items
    }
}

class OrderItemDataModel {
    lateinit var id: String
    var quantity: Int = 0
    lateinit var price: String

    constructor()

    constructor(id: String, quantity: Int, price: String) {
        this.id = id
        this.quantity = quantity
        this.price = price
    }
}

