package com.devlhse.service

import com.devlhse.model.Snippet
import java.util.*

class SnippetServiceImpl: SnippetService{

    override fun getSnippets(): MutableList<Snippet> {
        return Collections.synchronizedList(mutableListOf(
            Snippet(text = "hello"),
            Snippet(text = "world")
        ))
    }

}
