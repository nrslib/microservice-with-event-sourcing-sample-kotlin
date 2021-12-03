package com.example.ec.eventsourcing.cosmos

import com.azure.cosmos.CosmosClient
import com.azure.spring.data.cosmos.repository.config.EnableCosmosRepositories
import com.example.ec.eventsourcing.core.AggregateStore
import com.example.ec.eventsourcing.core.snapshot.SnapshotManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CosmosEventSourcingConfig(
    @Value("\${event-sourcing.cosmos.database-name}") private val databaseName: String,
    @Value("\${event-sourcing.cosmos.document-name:event_sourcing}") private val documentName: String,
) {
    @Bean
    fun aggregateStore(
        client: CosmosClient,
        snapshotManager: SnapshotManager
    ): AggregateStore {
        return CosmosAggregateStore(client, snapshotManager, databaseName, documentName)
    }

    @Bean
    fun snapshotManager(): SnapshotManager {
        return SnapshotManager()
    }
}