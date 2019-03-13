package com.devlhse.service

import com.devlhse.model.PostSnippet
import com.devlhse.model.Snippet

interface SnippetService {
    suspend fun getSnippets(): List<Snippet>
    suspend fun getSnippet(id: Int): Snippet?
    suspend fun createSnippet(snippet: PostSnippet): Snippet
    suspend fun updateSnippet(snippet: PostSnippet): Snippet?
    suspend fun deleteSnippet(id: Int): Boolean
}