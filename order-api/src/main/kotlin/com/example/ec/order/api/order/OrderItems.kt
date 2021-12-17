package com.example.ec.order.api.order

import com.example.ec.shared.money.models.money.Money

class OrderItems(val items: List<OrderItem>) {
    fun total(): Money = items.fold(Money.zero()) { acc, item -> acc + item.price }
}