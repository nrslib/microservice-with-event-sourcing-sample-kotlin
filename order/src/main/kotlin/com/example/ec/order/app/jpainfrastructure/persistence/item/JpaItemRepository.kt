package com.example.ec.order.app.jpainfrastructure.persistence.item

import com.example.ec.order.app.domain.models.item.Item
import com.example.ec.order.app.domain.models.item.ItemId
import com.example.ec.order.app.domain.models.item.ItemRepository
import com.example.ec.order.app.jpainfrastructure.jpa.item.ItemDataModel
import com.example.ec.order.app.jpainfrastructure.jpa.item.ItemDataModelJpaRepository
import com.example.ec.shared.money.models.money.Money

class JpaItemRepository(private val jpaRepository: ItemDataModelJpaRepository) : ItemRepository {
    override fun find(itemId: ItemId): Item? {
        val target = jpaRepository.findById(itemId.value)

        return target.map(this::convert).orElse(null)
    }

    override fun find(itemId: List<ItemId>): List<Item> {
        TODO("Not yet implemented")
    }

    override fun save(item: Item) {
        TODO("Not yet implemented")
    }

    fun convert(data: ItemDataModel): Item {
        return Item(
            ItemId(data.id),
            Money(data.price),
            data.name
        )
    }
}