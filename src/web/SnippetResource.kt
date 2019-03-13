package com.devlhse.web

import com.devlhse.model.PostSnippet
import com.devlhse.service.SnippetService
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.delete

fun Route.snippet(snippetService: SnippetService) {

    authenticate {
        route("/snippets") {
            get {
                call.application.environment.log.info("Searching for snippets...")
                call.respond(snippetService.getSnippets())
            }
            get("/{id}") {
                val id = call.parameters["id"]!!.toInt()
                call.application.environment.log.info("Searching for snippet id: $id")
                val snippet = snippetService.getSnippet(id)
                if (snippet == null) call.respond(HttpStatusCode.NotFound)
                call.respond(HttpStatusCode.Created, mapOf("snippet" to snippet))
            }
            post {
                call.application.environment.log.info("Creating new snippet...")
                val post = call.receive<PostSnippet>()
                val snippetCreated = snippetService.createSnippet(post)
                call.respond(HttpStatusCode.Created, mapOf("snippet" to snippetCreated))
            }
            put {
                val snippet = call.receive<PostSnippet>()
                val updated = snippetService.updateSnippet(snippet)
                if (updated == null) call.respond(HttpStatusCode.NotFound)
                else call.respond(HttpStatusCode.OK, updated)
            }
            delete("/{id}") {
                val id = call.parameters["id"]!!.toInt()
                call.application.environment.log.info("Deleting snippet id: $id")
                val removed = snippetService.deleteSnippet(id)
                if (removed) call.respond(HttpStatusCode.OK)
                else call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}