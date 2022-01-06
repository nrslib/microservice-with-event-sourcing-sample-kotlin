package com.example.ec.eventsourcing.core.event

class EventWithMetaData(
    val event: Event,
    val version: Long
)