package com.example.ec.eventsourcing.jpa.event

import javax.persistence.*

@Entity
@Table(name = "events")
class EventDataModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = -1
    lateinit var eventType: String
    lateinit var entityType: String
    lateinit var entityId: String
    lateinit var payload: String

    constructor()

    constructor(eventType: String, entityType: String, entityId: String, payload: String) {
        this.eventType = eventType
        this.entityType = entityType
        this.entityId = entityId
        this.payload = payload
    }
}