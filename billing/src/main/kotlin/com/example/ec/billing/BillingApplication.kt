package com.example.ec.billing

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BillingApplication

fun main(args: Array<String>) {
    runApplication<BillingApplication>(*args)
}
