package com.example.ec.eventsourcing.dynamo.event

import com.example.ec.eventsourcing.dynamo.document.DocumentType
import com.example.ec.eventsourcing.dynamo.document.EventSourcingDocument
import com.google.gson.Gson

class EventDataModel {
    lateinit var id: String
    lateinit var eventType: String
    lateinit var entityType: String
    lateinit var entityId: String
    lateinit var payload: String
    var version: Long = -1

    constructor()

    constructor(id: String, eventType: String, entityType: String, entityId: String, version: Long, payload: String) {
        this.id = id
        this.eventType = eventType
        this.entityType = entityType
        this.entityId = entityId
        this.version = version
        this.payload = payload
    }

    fun toDocument(): EventSourcingDocument {
        val document = EventSourcingDocument()

        document.id = id
        document.documentType = DocumentType.Event
        document.entityId = entityId
        document.version = version
        document.documentTypeToVersion = document.documentType.toString() + "#" + document.version
        document.body = Gson().toJson(this)

        return document
    }

    companion object {
        fun from(source: EventSourcingDocument): EventDataModel {
            if (source.documentType != DocumentType.Event) {
                throw Exception()
            }

            return Gson().fromJson(source.body, EventDataModel::class.java)
        }
    }
}