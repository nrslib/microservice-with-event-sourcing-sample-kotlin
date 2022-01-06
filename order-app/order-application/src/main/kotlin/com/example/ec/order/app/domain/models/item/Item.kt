package com.example.ec.order.app.domain.models.item

import com.example.ec.shared.money.models.money.Money

class Item(
    val id: ItemId,
    val price: Money,
    val name: String
)