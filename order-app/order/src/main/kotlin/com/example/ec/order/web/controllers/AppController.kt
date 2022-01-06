package com.example.ec.order.web.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AppController {
    @GetMapping
    fun index(): String {
        return "hello"
    }
}