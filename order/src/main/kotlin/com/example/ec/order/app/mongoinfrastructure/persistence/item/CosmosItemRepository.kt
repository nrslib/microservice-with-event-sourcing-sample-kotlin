package com.example.ec.order.app.mongoinfrastructure.persistence.item

import com.example.ec.order.app.domain.models.item.Item
import com.example.ec.order.app.domain.models.item.ItemId
import com.example.ec.order.app.domain.models.item.ItemRepository
import com.example.ec.order.app.mongoinfrastructure.cosmos.item.ItemDataModel
import com.example.ec.order.app.mongoinfrastructure.cosmos.item.ReactiveCosmosItemRepository
import com.example.ec.shared.money.models.money.Money

class CosmosItemRepository(private val cosmosRepository: ReactiveCosmosItemRepository) : ItemRepository {
    override fun find(itemId: ItemId): Item? {
        val target = cosmosRepository.findById(itemId.value)

        return target.map(this::convert).block()
    }

    override fun find(itemId: List<ItemId>): List<Item> {
        return itemId.map(this::find).filterNotNull()
    }

    override fun save(item: Item) {
        val dataModel = convert(item)
        cosmosRepository.save(dataModel).block()
    }

    private fun convert(dataModel: ItemDataModel): Item {
        return Item(
            ItemId(dataModel.id),
            Money(dataModel.price),
            dataModel.name
        )
    }

    private fun convert(item: Item): ItemDataModel {
        return ItemDataModel(
            item.id.value,
            item.price.amount.toString(),
            item.name
        )
    }
}