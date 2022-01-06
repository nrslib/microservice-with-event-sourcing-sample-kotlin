package com.example.ec.order.infrastructure.cosmos.datamodel.item

import com.azure.spring.data.cosmos.repository.ReactiveCosmosRepository

interface ReactiveCosmosItemRepository : ReactiveCosmosRepository<ItemDataModel, String>