package com.example.ec.eventsourcing.jpa.entity

import java.io.Serializable

class EntityKey : Serializable {
    lateinit var type: String
    lateinit var id: String

    constructor()

    constructor(type: String, id: String) {
        this.type = type
        this.id = id
    }
}