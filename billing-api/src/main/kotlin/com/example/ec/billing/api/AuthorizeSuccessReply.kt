package com.example.ec.billing.api

class AuthorizeSuccessReply {
    lateinit var orderId: String

    constructor()

    constructor(orderId: String) {
        this.orderId = orderId
    }
}