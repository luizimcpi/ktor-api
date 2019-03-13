package com.devlhse.service

import com.devlhse.model.User

interface AuthService {
    fun getUsers(): MutableMap<String?, User>
}