package com.example.ec.eventsourcing.jpa.entity

import javax.persistence.*

@Entity
@IdClass(value = EntityKey::class)
@Table(name = "entities")
class EntityDataModel {
    @Id
    lateinit var type: String

    @Id
    lateinit var id: String

    @Column(nullable = false)
    lateinit var version: String
}