package com.example.ec.order.web.controllers

import com.example.ec.applicationsupportstack.applicationsupport.exceptions.NotFoundException
import com.example.ec.order.app.application.order.OrderApplicationService
import com.example.ec.order.app.domain.models.order.Order
import com.example.ec.order.web.models.order.post.OrderPostRequest
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/order")
class OrderController(private val orderApplicationService: OrderApplicationService) {
    @GetMapping
    fun get(): String {
        return "order api"
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: String): Order {
        val order = orderApplicationService.findOrder(id) ?: throw NotFoundException("${id} is notfound")

        return order
    }

    @PostMapping
    fun post(@RequestBody body: OrderPostRequest): String {
        val createdOrderId = orderApplicationService.createOrder(body.accountId, body.items)

        return createdOrderId
    }

    @PostMapping("/{id}/approve")
    fun approve(@PathVariable id: String): String {
        val approvedOrderId = orderApplicationService.approveOrder(id)

        return approvedOrderId
    }

    @PostMapping("/{id}/reject")
    fun reject(@PathVariable id: String): String {
        val rejectedOrderId = orderApplicationService.rejectOrder(id)

        return rejectedOrderId
    }
}