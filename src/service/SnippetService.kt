package com.devlhse.service

import com.devlhse.model.Snippet

interface SnippetService {
    fun getSnippets(): MutableList<Snippet>
}