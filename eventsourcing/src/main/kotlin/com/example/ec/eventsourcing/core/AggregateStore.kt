package com.example.ec.eventsourcing.core

import com.example.ec.eventsourcing.core.entity.EntityIdAndVersion
import com.example.ec.eventsourcing.core.entity.EntityWithMetadata
import com.example.ec.eventsourcing.core.event.Event
import com.example.ec.eventsourcing.core.event.EventTypeAndData
import com.example.ec.eventsourcing.core.event.EventWithMetaData
import com.example.ec.eventsourcing.core.snapshot.Snapshot

interface AggregateStore {
    fun <T> save(
        clazz: Class<T>,
        events: List<EventTypeAndData>,
        options: AggregateCrudSaveOptions? = null
    ): EntityIdAndVersion where T : Aggregate<T>

    fun <T> find(clazz: Class<T>, id: String): EntityWithMetadata<T>? where T : Aggregate<T>
    fun <T> update(
        clazz: Class<T>,
        entityIdAndVersion: EntityIdAndVersion,
        events: List<Event>,
        options: UpdateOptions? = null
    ): EntityIdAndVersion where T : Aggregate<T>

    fun possiblySnapshot(
        aggregate: Aggregate<*>,
        snapshotVersion: String?,
        oldEvents: List<EventWithMetaData>,
        newEvents: List<Event>,
    ): Snapshot?

    fun <T> recreateAggregate(clasz: Class<T>, snapshot: Snapshot): T where T : Aggregate<T>
}