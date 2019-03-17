package com.devlhse.service

import com.devlhse.model.Snippets
import com.devlhse.model.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.config.ApplicationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init(enviroment: ApplicationConfig) {
        Database.connect(hikari(enviroment))
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
                it[password] = "VGVzdEAxMjM0YWNiZmZjMzAtMWQ4YS00ZDg1LWFmZTctMDhiYWI2MTIyOTU2" // BASE64 (Test@1234acbffc30-1d8a-4d85-afe7-08bab6122956)
                it[salt] = "acbffc30-1d8a-4d85-afe7-08bab6122956"
                it[dateUpdated] = System.currentTimeMillis()
            }
        }
    }

    private fun hikari(enviroment: ApplicationConfig): HikariDataSource {
        val config = HikariConfig()
//        Mysql configs
//        config.driverClassName = enviroment.property("database.mysql.driver.class.name").getString()
//        config.jdbcUrl = enviroment.property("database.mysql.jdbc.url").getString()
//        config.username = enviroment.property("database.mysql.username").getString()
//        config.password = enviroment.property("database.mysql.password").getString()
        config.driverClassName = enviroment.property("database.h2.driver.class.name").getString()
        config.jdbcUrl = enviroment.property("database.h2.jdbc.url").getString()
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = enviroment.property("database.transaction.isolation").getString()
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