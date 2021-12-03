package com.example.ec.eventsourcing.core.entity

import com.example.ec.eventsourcing.core.event.EventWithMetaData

class EntityWithMetadata<T>(
    val idAndVersion: EntityIdAndVersion,
    val events: List<EventWithMetaData>,
    val entity: T,
    val snapshotVersion: String? = null
)