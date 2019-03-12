package com.devlhse

import com.devlhse.exception.InvalidCredentialsException
import com.devlhse.model.PostSnippet
import com.devlhse.model.SimpleJWT
import com.devlhse.model.Snippet
import com.devlhse.model.User
import com.devlhse.service.SnippetServiceImpl
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.jwt.jwt
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.routing.*
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

    //TODO criar cadastro de users e extrair essa responsabilidade para um service
    val users = Collections.synchronizedMap(
        listOf(User("luizhse", "Test@1234"))
            .associateBy { it.name }
            .toMutableMap()
    )

    val snippetService = SnippetServiceImpl()

    routing {
        get("/health") {
            call.respond(mapOf("status" to "OK"))
        }

        post("/auth/token") {
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
                    call.respond(mapOf("snippets" to synchronized(snippetService.getSnippets()) { snippetService.getSnippets().toList() }))
                }
                post {
                    val post = call.receive<PostSnippet>()
                    snippetService.getSnippets() += Snippet(text = post.snippet.text)
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


