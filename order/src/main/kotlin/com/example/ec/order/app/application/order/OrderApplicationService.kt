package com.example.ec.order.app.application.order

import com.example.ec.applicationsupportstack.kotlinsupport.AllOpen
import com.example.ec.eventsourcing.core.AggregateRepository
import com.example.ec.order.app.domain.models.item.Item
import com.example.ec.order.app.domain.models.item.ItemId
import com.example.ec.order.app.domain.models.item.ItemRepository
import com.example.ec.order.app.domain.models.order.*
import org.springframework.transaction.annotation.Transactional

@AllOpen
class OrderApplicationService(
    private val itemRepository: ItemRepository,
    private val orderRepository: AggregateRepository<Order, OrderCommand>
) {
    fun findOrder(orderId: String): Order? {
        val orderWithMeta = orderRepository.find(orderId) ?: return null

        return orderWithMeta.entity
    }

    @Transactional
    fun createOrder(accountId: String, itemAndQuantities: List<ItemIdAndQuantity>): String {
        val items = itemRepository.find(itemAndQuantities.map { ItemId(it.itemId) })
        val orderItems = makeOrderItems(itemAndQuantities, items)
        val command = CreateOrderCommand(accountId, orderItems)
        val result = orderRepository.save(command)

        return result.idAndVersion.id
    }

    @Transactional
    fun approveOrder(orderId: String): String {
        val approvedOrder = orderRepository.update(orderId, ApproveOrderCommand())

        return approvedOrder.idAndVersion.id
    }

    @Transactional
    fun rejectOrder(orderId: String): String {
        val rejectedOrder = orderRepository.update(orderId, RejectOrderCommand())

        return rejectedOrder.idAndVersion.id
    }

    private fun makeOrderItems(itemAndQuantities: List<ItemIdAndQuantity>, items: List<Item>): OrderItems {
        val orderItems = itemAndQuantities.map {
            val item = items.first { item -> item.id.value == it.itemId }

            OrderItem(
                it.quantity,
                item.price
            )
        }

        return OrderItems(orderItems)
    }
}