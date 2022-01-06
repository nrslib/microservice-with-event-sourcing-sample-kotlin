package com.example.ec.eventsourcing.core.snapshot

import com.example.ec.eventsourcing.core.Aggregate
import com.example.ec.eventsourcing.core.event.Event
import com.example.ec.eventsourcing.core.event.EventWithMetaData

interface SnapshotStrategy {
    fun getAggregateClass(): Class<*>

    fun possiblySnapshot(
        aggregate: Aggregate<*>,
        oldEvents: List<EventWithMetaData>,
        newEvents: List<Event>,
        snapshotVersion: String? = null
    ): Snapshot?

    fun <T> recreateAggregate(
        clasz: Class<T>,
        snapshot: Snapshot
    ): Aggregate<T> where T : Aggregate<T>
}