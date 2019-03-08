package com.devlhse.model

data class Snippet(val text: String)

data class PostSnippet(val snippet: PostSnippet.Text) {
    data class Text(val text: String)
}
