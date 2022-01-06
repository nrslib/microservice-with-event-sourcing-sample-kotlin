package com.example.ec.order.configs.debug

import com.example.ec.eventsourcing.core.snapshot.SnapshotManager
import com.example.ec.order.app.domain.models.item.ItemRepository
import com.example.ec.order.app.domain.models.order.OrderSnapshotStrategy
import com.example.ec.order.configs.debug.strategy.JpaEsNamingStrategy
import com.example.ec.order.infra.jpainfrastructure.jpa.item.ItemDataModelJpaRepository
import com.example.ec.order.infra.jpainfrastructure.persistence.item.JpaItemRepository
import org.hibernate.boot.model.naming.PhysicalNamingStrategy
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@Profile("debug")
@EnableJpaRepositories(basePackages = ["com.example.ec.order.infra.jpainfrastructure"])
@EntityScan(basePackages = ["com.example.ec.order.infra.jpainfrastructure"])
class InfrastructureConfig() {
    @Bean
    fun jpaEsNamingStrategy(): PhysicalNamingStrategy {
        return JpaEsNamingStrategy("order")
    }

    @Bean
    fun itemRepository(itemDataModelJpaRepository: ItemDataModelJpaRepository): ItemRepository {
        return JpaItemRepository(itemDataModelJpaRepository)
    }

    @Primary
    @Bean
    fun snapshotManager(): SnapshotManager {
        val manager = SnapshotManager()
        manager.add(OrderSnapshotStrategy())
        return manager
    }
}
