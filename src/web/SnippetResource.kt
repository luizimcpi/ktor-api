package com.devlhse.web

import com.devlhse.model.PostSnippet
import com.devlhse.model.Snippet
import com.devlhse.service.SnippetService
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*

fun Route.snippet(snippetService: SnippetService) {

    authenticate {
        //Snippets Route
        route("/snippets") {
            get {
                call.application.environment.log.info("Searching for snippets...")
                call.respond(mapOf("snippets" to synchronized(snippetService.getSnippets()) { snippetService.getSnippets().toList() }))
            }
            post {
                call.application.environment.log.info("Creating new snippet...")
                val post = call.receive<PostSnippet>()
                snippetService.getSnippets() += Snippet(text = post.snippet.text)
                call.respond(HttpStatusCode.Created, mapOf("CREATED" to true))
            }
            delete("/{id}") {
                val id = call.parameters["id"]
                call.application.environment.log.info("Deleting snippet id: $id")
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }

}