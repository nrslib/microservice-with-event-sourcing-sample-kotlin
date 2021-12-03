package com.example.ec.applicationsupportstack.domainsupport.valueobjects

open class ValueObject<T>(
        val value: T
) {
    init {
        requireNotNull(value)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ValueObject<*>

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value?.hashCode() ?: 0
    }

    override fun toString(): String {
        return value.toString()
    }
}