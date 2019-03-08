package com.devlhse.model

import java.util.*

data class Snippet(val id: String = UUID.randomUUID().toString(), val text: String)

data class PostSnippet(val snippet: PostSnippet.Text) {
    data class Text(val text: String)
}
