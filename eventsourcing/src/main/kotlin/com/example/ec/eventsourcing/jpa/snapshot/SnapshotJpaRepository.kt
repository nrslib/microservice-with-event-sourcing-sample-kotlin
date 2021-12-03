package com.example.ec.eventsourcing.jpa.snapshot

import org.springframework.data.repository.CrudRepository

interface SnapshotJpaRepository : CrudRepository<SnapshotDataModel, SnapshotKey> {
    fun findFirstByEntityIdAndEntityTypeOrderByEntityVersionDesc(
        entityId: String,
        entityType: String
    ): SnapshotDataModel?
}