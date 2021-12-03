package com.example.ec.eventsourcing.core.entity

class EntityIdVersionAndEventIds(
    val entityId: String,
    val entityVersion: Long,
    val eventIds: List<Long>
)