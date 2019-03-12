package com.devlhse

import com.auth0.jwk.JwkProviderBuilder
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
import java.util.concurrent.TimeUnit

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

    //TODO extrair essa responsabilidade para um service
    val snippets = Collections.synchronizedList(mutableListOf(
        Snippet(text = "hello"),
        Snippet(text = "world")
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
            val post = call.receiveParameters()
            val postUser = post["user"]
            val postPassword = post["password"]

            val user = users.getOrPut(postUser) { User(postUser!!, postPassword!!) }
            if (user.password != postPassword) throw InvalidCredentialsException("Invalid credentials")
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
                    snippets += Snippet(text = post.snippet.text)
                    call.respond(HttpStatusCode.Created, mapOf("CREATED" to true))
                }
                delete("/{id}") {
                    val id = call.parameters["id"]
                    println("Delete Snippet id >>>: $id")
                    call.respond(HttpStatusCode.NoContent)
                }
            }
        }
    }
}

