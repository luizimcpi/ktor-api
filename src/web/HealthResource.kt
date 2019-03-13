package com.devlhse.web

import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get

fun Route.health() {

    get("/health") {
        call.respond(mapOf("status" to "OK"))
    }

}