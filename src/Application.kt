package com.devlhse

import com.devlhse.exception.InvalidCredentialsException
import com.devlhse.exception.SnippetsNotFoundException
import com.devlhse.model.SimpleJWT
import com.devlhse.service.AuthServiceImpl
import com.devlhse.service.DatabaseFactory
import com.devlhse.service.SnippetServiceImpl
import com.devlhse.web.auth
import com.devlhse.web.health
import com.devlhse.web.snippet
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.routing.routing

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header("MyCustomHeader")
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

    install(StatusPages) {
        exception<InvalidCredentialsException> { exception ->
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to (exception.message ?: "")))
        }
        exception<SnippetsNotFoundException> { exception ->
            call.respond(HttpStatusCode.NotFound, mapOf("error" to (exception.message ?: "")))
        }
    }

    val simpleJwt = SimpleJWT(environment.config.property("jwt.secret").getString())
    install(Authentication) {
        jwt {
            verifier(simpleJwt.verifier)
            validate {
                UserIdPrincipal(it.payload.getClaim("name").asString())
            }
        }
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    DatabaseFactory.init(environment.config)
    val snippetService = SnippetServiceImpl()
    val authService = AuthServiceImpl()

    routing {
        health()
        auth(authService, simpleJwt)
        snippet(snippetService)
    }
}