package com.example.ec.order.infra.jpainfrastructure.jpa.item

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "items")
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