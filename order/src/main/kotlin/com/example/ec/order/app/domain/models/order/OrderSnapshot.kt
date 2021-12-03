package com.example.ec.order.app.domain.models.order

import com.example.ec.eventsourcing.core.snapshot.Snapshot
import com.example.ec.order.api.OrderState
import com.example.ec.shared.money.models.money.Money

class OrderSnapshot : Snapshot {
    lateinit var accountId: String
    lateinit var total: Money
    lateinit var items: OrderItems
    lateinit var orderState: OrderState
}