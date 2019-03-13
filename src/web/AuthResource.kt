package com.devlhse.web

import com.devlhse.exception.InvalidCredentialsException
import com.devlhse.model.SimpleJWT
import com.devlhse.model.User
import com.devlhse.service.AuthService
import io.ktor.application.call
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post

fun Route.auth(authService: AuthService, simpleJwt: SimpleJWT) {

    post("/auth/login") {
        val post = call.receiveParameters()
        val postUser = post["user"]
        val postPassword = post["password"]

        val user = authService.getUsers().getOrPut(postUser) { User(postUser!!, postPassword!!) }
        if (user.password != postPassword) throw InvalidCredentialsException("Invalid credentials")
        call.application.environment.log.info("Passando pela autenticação...")
        call.respond(mapOf("token" to simpleJwt.sign(user.name)))
    }
}