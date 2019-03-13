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