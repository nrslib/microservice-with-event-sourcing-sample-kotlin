package com.example.ec.order.infrastructure.cosmos.datamodel.item

import com.azure.spring.data.cosmos.core.mapping.Container
import org.springframework.data.annotation.Id

@Container(containerName = "order_items")
class ItemDataModel {
    @Id
    lateinit var id: String
    lateinit var price: String
    lateinit var name: String

    constructor()

    constructor(id: String, price: String, name: String) {
        this.id = id
        this.price = price
        this.name = name
    }
}