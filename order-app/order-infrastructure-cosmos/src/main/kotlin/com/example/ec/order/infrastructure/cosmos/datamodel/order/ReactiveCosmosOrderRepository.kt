package com.example.ec.order.infrastructure.cosmos.datamodel.order

import com.azure.spring.data.cosmos.repository.ReactiveCosmosRepository
import com.example.ec.order.app.mongoinfrastructure.cosmos.order.OrderDataModel

interface ReactiveCosmosOrderRepository : ReactiveCosmosRepository<OrderDataModel, String>