package com.devlhse.service

import com.devlhse.model.User

interface AuthService {
    suspend fun getUsers(): MutableMap<String?, User>
}