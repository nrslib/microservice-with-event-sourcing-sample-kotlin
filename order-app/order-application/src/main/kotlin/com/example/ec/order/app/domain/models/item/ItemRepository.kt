package com.example.ec.order.app.domain.models.item

interface ItemRepository {
    fun find(itemId: ItemId): Item?
    fun find(itemId: List<ItemId>): List<Item>
    fun save(item: Item)
}