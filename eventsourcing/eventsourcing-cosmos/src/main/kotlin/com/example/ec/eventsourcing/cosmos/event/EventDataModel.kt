package com.example.ec.eventsourcing.cosmos.event

import com.example.ec.eventsourcing.cosmos.document.DocumentType
import com.example.ec.eventsourcing.cosmos.document.EventSourcingDocument
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
        document.type = DocumentType.Event
        document.entityId = entityId
        document.version = version
        document.body = Gson().toJson(this)

        return document
    }

    companion object {
        fun from(source: EventSourcingDocument): EventDataModel {
            if (source.type != DocumentType.Event) {
                throw Exception()
            }

            return Gson().fromJson(source.body, EventDataModel::class.java)
        }
    }
}