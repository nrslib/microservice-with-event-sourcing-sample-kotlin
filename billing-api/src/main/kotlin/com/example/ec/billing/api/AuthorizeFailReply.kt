package com.example.ec.billing.api

class AuthorizeFailReply {
    lateinit var orderId: String

    constructor()

    constructor(orderId: String) {
        this.orderId = orderId
    }
}