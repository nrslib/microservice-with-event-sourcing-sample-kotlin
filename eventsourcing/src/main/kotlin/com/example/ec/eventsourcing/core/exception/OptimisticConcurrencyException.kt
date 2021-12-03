package com.example.ec.eventsourcing.core.exception

class OptimisticConcurrencyException(val id: String, val expectedVersion: Long, val actualVersion: Long) :
    Exception("expected id $id to have version $expectedVersion but was $actualVersion")