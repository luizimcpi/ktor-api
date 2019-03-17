package com.devlhse

import io.ktor.config.MapApplicationConfig
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApplicationTest {
    @Test
    fun testHealthStatus() {
        withTestApplication({
            (environment.config as MapApplicationConfig).apply {
                put("jwt.secret", "devlhse-ktor-api-jwt")
                put("database.mysql.driver.class.name", "mysqlDriverClassNameTest")
                put("database.mysql.jdbc.url", "mysqlJdbcUrlTest")
                put("database.mysql.username", "usernameTest")
                put("database.mysql.password", "passwordTest")
                put("database.h2.driver.class.name", "org.h2.Driver")
                put("database.h2.jdbc.url", "jdbc:h2:mem:test")
                put("database.transaction.isolation", "TRANSACTION_REPEATABLE_READ")
            }
            module()
        }) {
            handleRequest(HttpMethod.Get, "/health").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertTrue(response.content != null)
            }
        }
    }
}



