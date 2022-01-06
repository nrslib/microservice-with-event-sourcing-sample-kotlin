package com.example.ec.order

import com.example.ec.eventsourcing.dynamo.DynamoEventSourcingConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(DynamoEventSourcingConfig::class)
//@Import(JpaEventSourcingConfig::class)
class OrderApplication

fun main(args: Array<String>) {
    runApplication<OrderApplication>(*args)
}
