package com.example.ec.eventsourcing.dynamo.entity

import com.example.ec.eventsourcing.dynamo.document.DocumentType
import com.example.ec.eventsourcing.dynamo.document.EventSourcingDocument
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
        document.documentType = DocumentType.Entity
        document.entityId = id
        document.version = version
        document.documentTypeToVersion = document.documentType.toString()
        document.body = Gson().toJson(this)

        return document
    }

    companion object {
        fun from(source: EventSourcingDocument): EntityDataModel {
            if (source.documentType != DocumentType.Entity) {
                throw Exception()
            }

            return Gson().fromJson(source.body, EntityDataModel::class.java)
        }
    }
}