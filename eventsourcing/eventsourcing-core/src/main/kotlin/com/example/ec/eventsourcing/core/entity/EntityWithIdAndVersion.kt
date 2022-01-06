package com.example.ec.eventsourcing.core.entity

class EntityWithIdAndVersion<T>(
    val idAndVersion: EntityIdAndVersion,
    val aggregate: T
)