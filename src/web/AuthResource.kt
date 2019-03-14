package com.devlhse.web

import com.devlhse.exception.InvalidCredentialsException
import com.devlhse.model.SimpleJWT
import com.devlhse.model.encrypt
import com.devlhse.service.AuthService
import com.devlhse.web.request.LoginUserRequest
import com.devlhse.web.response.TokenResponse
import io.ktor.application.call
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post

fun Route.auth(authService: AuthService, simpleJwt: SimpleJWT) {

    post("/auth/login") {
        call.application.environment.log.info("Init login process...")
        val post = call.receiveParameters()
        val loginRequest = LoginUserRequest(post["username"].orEmpty(), post["password"].orEmpty())

        val user = authService.getUserByName(loginRequest.username)
        if (user == null) throw InvalidCredentialsException("Invalid credentials")
        else
        call.application.environment.log.info("Encrypting received password: ${loginRequest.password} with salt: ${user.salt}")
        val encryptedPassword = encrypt("${loginRequest.password}${user.salt}")
        call.application.environment.log.info("encryptedPassword : $encryptedPassword")

        if (user.password != encryptedPassword) throw InvalidCredentialsException("Invalid credentials")
        call.application.environment.log.info("Login Success...")
        call.respond(TokenResponse(simpleJwt.sign(user.name)))
    }
}