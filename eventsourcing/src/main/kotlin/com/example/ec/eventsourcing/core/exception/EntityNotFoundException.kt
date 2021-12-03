package com.example.ec.eventsourcing.core.exception

class EntityNotFoundException(val entityType: String, val entityId: String) :
    Exception("aggregateType: $entityType, entityId: $entityId")