package com.example.ec.order.app.mongoinfrastructure.cosmos.order

import com.azure.spring.data.cosmos.repository.ReactiveCosmosRepository

interface ReactiveCosmosOrderRepository : ReactiveCosmosRepository<OrderDataModel, String>