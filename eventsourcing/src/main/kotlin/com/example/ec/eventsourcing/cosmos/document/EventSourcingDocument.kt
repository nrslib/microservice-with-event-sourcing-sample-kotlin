package com.example.ec.eventsourcing.cosmos.document

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id

enum class DocumentType {
    Entity,
    Event,
    Snapshot
}

class EventSourcingDocument {
    @Id
    lateinit var id: String
    lateinit var type: DocumentType
    lateinit var entityId: String
    lateinit var body: String
    var version: Long = -1

    @JsonProperty("_etag")
    var etag: String = ""
}