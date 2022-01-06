package com.example.ec.order.app.domain.models.order

import com.example.ec.eventsourcing.core.Aggregate
import com.example.ec.eventsourcing.core.event.Event
import com.example.ec.eventsourcing.core.event.EventWithMetaData
import com.example.ec.eventsourcing.core.snapshot.Snapshot
import com.example.ec.eventsourcing.core.snapshot.SnapshotStrategy

class OrderSnapshotStrategy : SnapshotStrategy {
    override fun getAggregateClass(): Class<*> {
        return Order::class.java
    }

    override fun possiblySnapshot(
        aggregate: Aggregate<*>,
        oldEvents: List<EventWithMetaData>,
        newEvents: List<Event>,
        snapshotVersion: String?
    ): Snapshot? {
        val order = aggregate as Order

        val snapshot = OrderSnapshot()
        snapshot.accountId = order.accountId
        snapshot.orderState = order.orderState
        snapshot.total = order.total
        snapshot.items = order.items

        return snapshot
    }

    override fun <T> recreateAggregate(clasz: Class<T>, snapshot: Snapshot): T where T : Aggregate<T> {
        val orderSnapshot = snapshot as OrderSnapshot

        val aggregate = Order()
        aggregate.accountId = orderSnapshot.accountId
        aggregate.orderState = orderSnapshot.orderState
        aggregate.total = orderSnapshot.total
        aggregate.items = orderSnapshot.items

        return aggregate as T
    }
}