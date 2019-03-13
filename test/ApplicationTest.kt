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



