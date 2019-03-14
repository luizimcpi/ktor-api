package com.devlhse.service

import com.devlhse.model.User

interface AuthService {
    suspend fun getUsers(): MutableMap<String?, User>
    suspend fun getUserByName(name: String): User?
}