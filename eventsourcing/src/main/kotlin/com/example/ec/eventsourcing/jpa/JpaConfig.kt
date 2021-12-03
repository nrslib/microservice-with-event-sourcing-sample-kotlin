package com.example.ec.eventsourcing.jpa

import com.example.ec.eventsourcing.core.AggregateStore
import com.example.ec.eventsourcing.core.snapshot.SnapshotManager
import com.example.ec.eventsourcing.jpa.entity.EntityJpaRepository
import com.example.ec.eventsourcing.jpa.event.EventDataModelJpaRepository
import com.example.ec.eventsourcing.jpa.snapshot.SnapshotJpaRepository
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(basePackages = ["com.example.ec.eventsourcing.jpa"])
@EntityScan(basePackages = ["com.example.ec.eventsourcing.jpa"])
class JpaConfig {
    @Bean
    fun jpaAggregateStore(
        eventRepository: EventDataModelJpaRepository,
        entityRepository: EntityJpaRepository,
        snapshotRepository: SnapshotJpaRepository,
        snapshotManager: SnapshotManager
    ): AggregateStore {
        return JpaAggregateStore(eventRepository, entityRepository, snapshotRepository, snapshotManager)
    }

    @Bean
    fun snapshotManager(): SnapshotManager {
        return SnapshotManager()
    }
}