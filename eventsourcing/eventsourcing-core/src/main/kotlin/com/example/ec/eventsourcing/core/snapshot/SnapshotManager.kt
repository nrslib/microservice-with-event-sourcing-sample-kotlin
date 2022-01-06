package com.example.ec.eventsourcing.core.snapshot

import com.example.ec.eventsourcing.core.Aggregate
import com.example.ec.eventsourcing.core.event.Event
import com.example.ec.eventsourcing.core.event.EventWithMetaData

class SnapshotManager {
    private val clazzToStrategy = HashMap<Class<*>, SnapshotStrategy>()

    fun add(snapshotStrategy: SnapshotStrategy) {
        clazzToStrategy.put(snapshotStrategy.getAggregateClass(), snapshotStrategy)
    }

    fun possiblySnapshot(
        aggregate: Aggregate<*>,
        oldEvents: List<EventWithMetaData>,
        newEvents: List<Event>,
        snapshotVersion: String?
    ): Snapshot? {
        val strategy = clazzToStrategy.get(aggregate::class.java)

        return strategy?.possiblySnapshot(aggregate, oldEvents, newEvents, snapshotVersion)
    }

    fun <T> recreateFromSnapshot(clazz: Class<T>, snapshot: Snapshot): T where T : Aggregate<T> {
        val strategy = clazzToStrategy.get(clazz)

        return strategy!!.recreateAggregate(clazz, snapshot) as T
    }
}