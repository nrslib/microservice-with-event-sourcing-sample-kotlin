package com.example.ec.order.infra.jpainfrastructure.persistence.item

import com.example.ec.applicationsupportstack.applicationsupport.exceptions.NotFoundException
import com.example.ec.order.app.domain.models.item.Item
import com.example.ec.order.app.domain.models.item.ItemId
import com.example.ec.order.app.domain.models.item.ItemRepository
import com.example.ec.order.infra.jpainfrastructure.jpa.item.ItemDataModel
import com.example.ec.order.infra.jpainfrastructure.jpa.item.ItemDataModelJpaRepository
import com.example.ec.shared.money.models.money.Money

class JpaItemRepository(private val jpaRepository: ItemDataModelJpaRepository) : ItemRepository {
    override fun find(itemId: ItemId): Item? {
        val target = jpaRepository.findById(itemId.value)

        return target.map(this::convert).orElse(null)
    }

    override fun find(itemIds: List<ItemId>): List<Item> {
        return itemIds.map { jpaRepository.findById(it.value) }
            .map{
                it.map {
                    Item(ItemId(it.id), Money(it.price), it.name)
                }.orElseThrow {
                    NotFoundException("id not found")
                }
            }
    }

    override fun save(item: Item) {
        jpaRepository.save(ItemDataModel(item.id.value, item.price.amount.toString(), item.name))
    }

    fun convert(data: ItemDataModel): Item {
        return Item(
            ItemId(data.id),
            Money(data.price),
            data.name
        )
    }
}