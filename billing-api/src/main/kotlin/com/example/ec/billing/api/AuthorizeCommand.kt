package com.example.ec.billing.api

import com.example.ec.shared.money.models.money.Money

class AuthorizeCommand {
  lateinit var orderId: String
  lateinit var consumerId: String
  lateinit var orderTotal: Money

  private constructor() {}

  constructor(orderId: String, consumerId: String, orderTotal: Money) {
    this.orderId = orderId
    this.consumerId = consumerId
    this.orderTotal = orderTotal
  }
}