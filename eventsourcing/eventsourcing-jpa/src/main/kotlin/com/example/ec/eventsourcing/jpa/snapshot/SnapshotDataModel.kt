package com.example.ec.eventsourcing.jpa.snapshot

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass
import javax.persistence.Table

@Entity
@IdClass(value = SnapshotKey::class)
@Table(name = "snapshots")
class SnapshotDataModel {
    @Id
    lateinit var entityType: String

    @Id
    lateinit var entityId: String

    @Id
    lateinit var entityVersion: String
    lateinit var snapshotType: String
    lateinit var snapshotData: String

    constructor()

    constructor(
        entityType: String,
        entityId: String,
        entityVersion: String,
        snapshotType: String,
        snapshotData: String
    ) {
        this.entityType = entityType
        this.entityId = entityId
        this.entityVersion = entityVersion
        this.snapshotType = snapshotType
        this.snapshotData = snapshotData
    }
}