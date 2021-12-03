package com.example.ec.order.configs.debug

import com.example.ec.applicationsupportstack.microservicesupport.saga.SagaManager
import com.example.ec.eventsourcing.core.AggregateRepository
import com.example.ec.eventsourcing.core.AggregateStore
import com.example.ec.order.app.application.order.OrderApplicationService
import com.example.ec.order.app.domain.models.item.ItemRepository
import com.example.ec.order.app.domain.models.order.Order
import com.example.ec.order.app.handler.OrderEventHandler
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
        jpaAggregateStore: AggregateStore
    ): OrderApplicationService {
        return OrderApplicationService(
            itemRepository,
            AggregateRepository(Order::class.java, jpaAggregateStore)
        )
    }
}