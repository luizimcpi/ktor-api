package com.devlhse.service

import com.devlhse.model.*
import com.devlhse.service.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*

class SnippetServiceImpl: SnippetService{

    private val listeners = mutableMapOf<Int, suspend (Notification<Snippet?>) -> Unit>()

    override suspend fun getSnippets(): List<Snippet> = dbQuery {
        Snippets.selectAll().map { toSnippet(it) }
    }

    override suspend fun createSnippet(snippet: PostSnippet): Snippet {
        var key = 0
        dbQuery {
            key = (Snippets.insert {
                it[text] = snippet.text
                it[dateUpdated] = System.currentTimeMillis()
            } get Snippets.id)!!
        }
        return getSnippet(key)!!.also {
            onChange(ChangeType.CREATE, key, it)
        }
    }

    override suspend fun getSnippet(id: Int): Snippet? = dbQuery {
        Snippets.select {
            (Snippets.id eq id)
        }.mapNotNull { toSnippet(it) }
            .singleOrNull()
    }

    override suspend fun deleteSnippet(id: Int): Boolean {
        return dbQuery {
            Snippets.deleteWhere { Snippets.id eq id } > 0
        }.also {
            if(it) onChange(ChangeType.DELETE, id)
        }
    }

    override suspend fun updateSnippet(snippet: PostSnippet): Snippet? {
        val id = snippet.id
        return if (id == null) {
            createSnippet(snippet)
        } else {
            dbQuery {
                Snippets.update({ Snippets.id eq id }) {
                    it[text] = snippet.text
                    it[dateUpdated] = System.currentTimeMillis()
                }
            }
            getSnippet(id).also {
                onChange(ChangeType.UPDATE, id, it)
            }
        }
    }

    private fun toSnippet(row: ResultRow): Snippet =
        Snippet(
            id = row[Snippets.id],
            text = row[Snippets.text],
            dateUpdated = row[Snippets.dateUpdated]
        )

    private suspend fun onChange(type: ChangeType, id: Int, entity: Snippet?=null) {
        listeners.values.forEach {
            it.invoke(Notification(type, id, entity))
        }
    }
}
