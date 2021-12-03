package com.example.ec.eventsourcing.core.common

class Int128(private val hi: Long, private val lo: Long) {
    fun asString(): String {
        return "%08X-%08X".format(hi, lo)
    }

    override fun toString(): String {
        return "Int128(${asString()})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Int128) return false

        if (hi != other.hi) return false
        if (lo != other.lo) return false

        return true
    }

    override fun hashCode(): Int {
        var result = hi.hashCode()
        result = 31 * result + lo.hashCode()
        return result
    }

    companion object {
        fun fromString(str: String): Int128 {
            val tokens = str.split("-")
            if (tokens.count() != 2) {
                throw IllegalArgumentException()
            }

            return Int128(tokens[0].toLong(), tokens[1].toLong())
        }
    }
}