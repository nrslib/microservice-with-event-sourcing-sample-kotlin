package com.example.ec.order.app.mongoinfrastructure.cosmos.item

import com.azure.spring.data.cosmos.repository.ReactiveCosmosRepository

interface ReactiveCosmosItemRepository : ReactiveCosmosRepository<ItemDataModel, String>