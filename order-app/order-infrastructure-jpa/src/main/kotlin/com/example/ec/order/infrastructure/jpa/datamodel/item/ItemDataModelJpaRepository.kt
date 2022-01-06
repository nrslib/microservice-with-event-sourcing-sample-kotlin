package com.example.ec.order.infra.jpainfrastructure.jpa.item

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ItemDataModelJpaRepository : JpaRepository<ItemDataModel, String> {
    @Query(value = "SELECT item FROM ItemDataModel item WHERE item.id IN (:itemIds)")
    fun findByWatchers(@Param("itemIds") itemIds: List<String>): List<ItemDataModel>
}