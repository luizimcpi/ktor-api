package com.devlhse.service

import com.devlhse.model.Snippets
import com.devlhse.model.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init() {
        Database.connect(hikari())
        transaction {
            create(Snippets)
            Snippets.insert {
                it[text] = "snippet one"
                it[dateUpdated] = System.currentTimeMillis()
            }
            Snippets.insert {
                it[text] = "snippet two"
                it[dateUpdated] = System.currentTimeMillis()
            }
            create(Users)
            Users.insert {
                it[name] = "luizhse"
                it[password] = "VGVzdEAxMjM0YWNiZmZjMzAtMWQ4YS00ZDg1LWFmZTctMDhiYWI2MTIyOTU2"
                it[salt] = "acbffc30-1d8a-4d85-afe7-08bab6122956"
                it[dateUpdated] = System.currentTimeMillis()
            }
        }
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = "org.h2.Driver"
        config.jdbcUrl = "jdbc:h2:mem:test"
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(
        block: () -> T
    ): T =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }
}