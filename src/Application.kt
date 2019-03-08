package com.devlhse

import com.devlhse.exception.InvalidCredentialsException
import com.devlhse.model.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.auth.*
import com.fasterxml.jackson.databind.*
import io.ktor.auth.jwt.jwt
import io.ktor.jackson.*
import java.util.*

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
            call.respond(HttpStatusCode.Unauthorized, mapOf("OK" to false, "error" to (exception.message ?: "")))
        }
    }

    //TODO verificar como pegar secret do application.conf
    val simpleJwt = SimpleJWT("devlhse-ktor-api-jwt")
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

    //TODO extrair essa responsabilidade para um service
    val snippets = Collections.synchronizedList(mutableListOf(
        Snippet("hello"),
        Snippet("world")
    ))

    //TODO criar cadastro de users e extrair essa responsabilidade para um service
    val users = Collections.synchronizedMap(
        listOf(User("luizhse", "Test@1234"))
            .associateBy { it.name }
            .toMutableMap()
    )

    routing {
        get("/health") {
            call.respond(mapOf("status" to "OK"))
        }

        post("/login-register") {
            val post = call.receive<LoginRegister>()
            val user = users.getOrPut(post.user) { User(post.user, post.password) }
            if (user.password != post.password) throw InvalidCredentialsException("Invalid credentials")
            call.respond(mapOf("token" to simpleJwt.sign(user.name)))
        }

        authenticate {
            //Snippets Route
            route("/snippets") {
                get {
                    call.respond(mapOf("snippets" to synchronized(snippets) { snippets.toList() }))
                }
                post {
                    val post = call.receive<PostSnippet>()
                    snippets += Snippet(post.snippet.text)
                    call.respond(mapOf("CREATED" to true))
                }
            }
        }
    }
}

