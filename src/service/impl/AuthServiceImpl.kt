package com.devlhse.service

import com.devlhse.model.User
import java.util.Collections

class AuthServiceImpl : AuthService {

    override fun getUsers(): MutableMap<String?, User> {
        return Collections.synchronizedMap(
            listOf(User("luizhse", "Test@1234"))
                .associateBy { it.name }
                .toMutableMap()
        )
    }
}
