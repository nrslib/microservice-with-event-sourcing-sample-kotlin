package com.example.ec.order.configs.debug

import com.azure.cosmos.ConsistencyLevel
import com.azure.cosmos.CosmosClient
import com.azure.cosmos.CosmosClientBuilder
import com.example.ec.eventsourcing.core.snapshot.SnapshotManager
import com.example.ec.order.app.domain.models.item.ItemRepository
import com.example.ec.order.app.domain.models.order.OrderSnapshotStrategy
import com.example.ec.order.app.mongoinfrastructure.cosmos.item.ReactiveCosmosItemRepository
import com.example.ec.order.app.mongoinfrastructure.persistence.item.CosmosItemRepository
import com.example.ec.order.configs.debug.strategy.JpaEsNamingStrategy
import org.hibernate.boot.model.naming.PhysicalNamingStrategy
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.data.jpa.repository.config.EnableJpaRepositories


@Configuration
@Profile("debug")
@EnableJpaRepositories(basePackages = ["com.example.ec.order"])
@EntityScan(basePackages = ["com.example.ec.order"])
class InfrastructureConfig(
    @Value("\${azure.cosmos.uri}") private val azureCosmosUri: String,
    @Value("\${azure.cosmos.key}") private val azureCosmosKey: String,
    @Value("\${azure.cosmos.database}") private val azureCosmosDatabase: String

) {
    @Bean
    fun jpaEsNamingStrategy(): PhysicalNamingStrategy {
        return JpaEsNamingStrategy("order")
    }

    @Bean
    fun cosmosClient(): CosmosClient {
        return CosmosClientBuilder().endpoint(azureCosmosUri)
            .key(azureCosmosKey)
            .consistencyLevel(ConsistencyLevel.EVENTUAL)
            .buildClient()
    }

    @Bean
    fun itemRepository(reactiveCosmosItemRepository: ReactiveCosmosItemRepository): ItemRepository {
        return CosmosItemRepository(reactiveCosmosItemRepository)
    }

    @Bean
    @Primary
    fun strategyManager(): SnapshotManager {
        val manager = SnapshotManager()
        manager.add(OrderSnapshotStrategy())
        return manager
    }
}
