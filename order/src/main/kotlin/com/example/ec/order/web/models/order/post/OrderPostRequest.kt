package com.example.ec.order.web.models.order.post

import com.example.ec.order.app.application.order.ItemIdAndQuantity

data class OrderPostRequest(val accountId: String, val items: List<ItemIdAndQuantity>)