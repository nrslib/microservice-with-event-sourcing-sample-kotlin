package com.example.ec.order.configs.debug.strategy

import org.hibernate.boot.model.naming.Identifier
import org.hibernate.boot.model.naming.PhysicalNamingStrategy
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy

class JpaEsNamingStrategy(
    private val tablePrefix: String,
    private val core: SpringPhysicalNamingStrategy = SpringPhysicalNamingStrategy()
) : PhysicalNamingStrategy by core {
    override fun toPhysicalTableName(name: Identifier?, jdbcEnvironment: JdbcEnvironment?): Identifier {
        val identifier = core.toPhysicalTableName(name, jdbcEnvironment)
        return prefix(identifier)
    }

    private fun prefix(identifier: Identifier): Identifier {
        return Identifier.toIdentifier("${tablePrefix}_" + identifier.text)
    }
}