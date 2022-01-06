package com.example.ec.eventsourcing.jpa.event

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface EventDataModelJpaRepository : CrudRepository<EventDataModel, Long> {
    @Query(value = "SELECT event FROM EventDataModel event WHERE event.entityId = :entityId AND event.entityType = :entityType ORDER BY event.id")
    fun findByEntity(@Param("entityId") entityId: String, @Param("entityType") entityType: String): List<EventDataModel>

    fun findByEntityTypeAndEntityIdAndIdGreaterThanOrderById(
        entityType: String,
        entityId: String,
        id: Long
    ): List<EventDataModel>
}