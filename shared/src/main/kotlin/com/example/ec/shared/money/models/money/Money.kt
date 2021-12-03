package com.example.ec.shared.money.models.money

import java.math.BigDecimal

class Money {
    var amount: BigDecimal

    companion object {
        fun zero(): Money {
            return Money(0)
        }
    }

    operator fun plus(delta: Money): Money {
        return Money(amount.add(delta.amount))
    }

    operator fun minus(delta: Money): Money {
        return Money(amount.minus(delta.amount))
    }

    fun multiply(x: Int): Money {
        return Money(amount.multiply(BigDecimal(x)))
    }

    constructor(): this(BigDecimal(0))
    constructor(amount: BigDecimal) {
        this.amount = amount
    }
    constructor(x: Int) : this(BigDecimal(x))
    constructor(x: String) : this(BigDecimal(x))
}