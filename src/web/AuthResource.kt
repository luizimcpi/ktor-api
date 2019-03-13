package com.devlhse.web

import com.devlhse.exception.InvalidCredentialsException
import com.devlhse.model.SimpleJWT
import com.devlhse.model.User
import com.devlhse.model.encrypt
import com.devlhse.service.AuthService
import io.ktor.application.call
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post

fun Route.auth(authService: AuthService, simpleJwt: SimpleJWT) {

    post("/auth/login") {
        call.application.environment.log.info("Init login process...")
        val post = call.receiveParameters()
        val postUser = post["user"]
        val postPassword = post["password"]

        val user = authService.getUsers().getOrPut(postUser) { User(id = null, name = postUser!!, password = postPassword!!, salt = null, dateUpdated = null) }
        call.application.environment.log.info("Encrypting received password: $postPassword with salt: ${user.salt}")
        val encryptedPassword = encrypt("$postPassword${user.salt}")
        call.application.environment.log.info("encryptedPassword : $encryptedPassword")

        if (user.password != encryptedPassword) throw InvalidCredentialsException("Invalid credentials")
        call.application.environment.log.info("Login Success...")
        call.respond(mapOf("token" to simpleJwt.sign(user.name)))
    }
}