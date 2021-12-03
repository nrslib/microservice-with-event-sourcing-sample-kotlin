package com.example.ec.order.configs.debug

import com.example.ec.applicationsupportstack.microservicesupport.saga.SagaManager
import com.example.ec.mq.Producer
import com.example.ec.order.app.application.order.OrderApplicationService
import com.example.ec.order.app.domain.models.item.ItemRepository
import com.example.ec.order.app.sagas.createorder.CreateOrderSaga
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile(*["debug"])
class Startup(
    sagaManager: SagaManager,
    @Qualifier("billingCommandProducer") producer: Producer,
    orderApplicationService: OrderApplicationService,
    itemRepository: ItemRepository
) {
    init {
        sagaManager.register(CreateOrderSaga(producer, orderApplicationService))

//        itemRepository.save(
//            Item(
//                ItemId("test-item-id"),
//                Money(200),
//                "test-item-name"
//            )
//        )
    }
}