package com.example.ec.eventsourcing.cosmos.entity

import com.example.ec.eventsourcing.cosmos.document.DocumentType
import com.example.ec.eventsourcing.cosmos.document.EventSourcingDocument
import com.google.gson.Gson

class EntityDataModel {
    lateinit var id: String
    lateinit var type: String
    var version: Long = -1

    constructor()

    constructor(id: String, type: String, version: Long) {
        this.id = id
        this.type = type
        this.version = version
    }

    fun toDocument(): EventSourcingDocument {
        val document = EventSourcingDocument()

        document.id = id
        document.type = DocumentType.Entity
        document.entityId = id
        document.version = version
        document.body = Gson().toJson(this)

        return document
    }

    companion object {
        fun from(source: EventSourcingDocument): EntityDataModel {
            if (source.type != DocumentType.Entity) {
                throw IllegalCallerException()
            }

            return Gson().fromJson(source.body, EntityDataModel::class.java)
        }
    }
}