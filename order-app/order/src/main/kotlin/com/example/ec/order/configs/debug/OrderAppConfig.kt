package com.example.ec.order.configs.debug

import com.example.ec.applicationsupportstack.microservicesupport.saga.SagaManager
import com.example.ec.eventsourcing.core.*
import com.example.ec.eventsourcing.core.entity.EntityIdAndVersion
import com.example.ec.eventsourcing.core.entity.EntityWithMetadata
import com.example.ec.eventsourcing.core.event.Event
import com.example.ec.eventsourcing.core.event.EventTypeAndData
import com.example.ec.eventsourcing.core.event.EventWithMetaData
import com.example.ec.eventsourcing.core.snapshot.Snapshot
import com.example.ec.order.app.application.order.OrderApplicationService
import com.example.ec.order.app.domain.models.item.ItemRepository
import com.example.ec.order.app.domain.models.order.Order
import com.example.ec.order.messaging.handlers.OrderEventHandler
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("debug")
class OrderAppConfig {
    @Bean
    fun orderEventHandler(sagaManager: SagaManager): OrderEventHandler {
        return OrderEventHandler(sagaManager)
    }

    @Bean
    fun sagaManager(): SagaManager {
        return SagaManager()
    }

    @Bean
    fun orderApplicationService(
        applicationEventPublisher: ApplicationEventPublisher,
        itemRepository: ItemRepository,
        aggregateStore: AggregateStore
    ): OrderApplicationService {
        return OrderApplicationService(
            itemRepository,
            AggregateRepository(Order::class.java, aggregateStore)
        )
    }
}