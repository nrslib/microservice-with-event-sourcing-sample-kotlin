package com.example.ec.eventsourcing.cosmos.snapshot

import com.example.ec.eventsourcing.cosmos.document.DocumentType
import com.example.ec.eventsourcing.cosmos.document.EventSourcingDocument
import com.google.gson.Gson

class SnapshotDataModel {
    lateinit var id: String
    lateinit var entityType: String
    lateinit var entityId: String
    var entityVersion: Long = -1
    lateinit var snapshotType: String
    lateinit var snapshotData: String

    constructor()

    constructor(
        id: String,
        entityType: String,
        entityId: String,
        entityVersion: Long,
        snapshotType: String,
        snapshotData: String
    ) {
        this.id = id
        this.entityType = entityType
        this.entityId = entityId
        this.entityVersion = entityVersion
        this.snapshotType = snapshotType
        this.snapshotData = snapshotData
    }

    fun toDocument(): EventSourcingDocument {
        val document = EventSourcingDocument()

        document.id = id
        document.type = DocumentType.Snapshot
        document.entityId = entityId
        document.version = entityVersion
        document.body = Gson().toJson(this)

        return document
    }

    companion object {
        fun from(document: EventSourcingDocument): SnapshotDataModel {
            if (document.type != DocumentType.Snapshot) {
                throw IllegalCallerException()
            }

            return Gson().fromJson(document.body, SnapshotDataModel::class.java)
        }
    }
}