package com.devlhse.model

import org.jetbrains.exposed.sql.Table

object Snippets : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val text = varchar("text", 255)
    val dateUpdated = long("dateUpdated")
}

data class Snippet(val id: Int, val text: String, val dateUpdated: Long)

data class PostSnippet(
    val id: Int?,
    val text: String
)