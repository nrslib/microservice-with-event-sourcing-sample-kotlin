package com.example.ec.eventsourcing.jpa.snapshot

import java.io.Serializable

class SnapshotKey : Serializable {
    lateinit var entityType: String
    lateinit var entityId: String
    lateinit var entityVersion: String

    constructor()

    constructor(entityId: String, entityType: String, entityVersion: String) {
        this.entityId = entityId
        this.entityType = entityType
        this.entityVersion = entityVersion
    }
}