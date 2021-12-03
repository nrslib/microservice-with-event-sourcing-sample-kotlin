package com.example.ec.order.app.domain.models.order

import com.example.ec.order.app.domain.models.item.Item
import com.example.ec.shared.money.models.money.Money

class ItemAndQuantity(val item: Item, val quantity: Int) {
    fun total(): Money {
        return item.price.multiply(quantity)
    }
}