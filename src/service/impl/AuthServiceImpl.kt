package com.devlhse.service

import com.devlhse.model.User
import com.devlhse.model.Users
import com.devlhse.service.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import java.util.Collections

class AuthServiceImpl : AuthService {

    override suspend fun getUserByName(name: String): User? = dbQuery {
        Users.select {
            (Users.name eq name)
        }.mapNotNull { toUser(it) }
            .singleOrNull()
    }

    override suspend fun getUsers(): MutableMap<String?, User> = dbQuery {
        Collections.synchronizedMap(Users.selectAll()
            .map { toUser(it) }
            .associateBy { it.name }
            .toMutableMap()
        )
    }

    private fun toUser(row: ResultRow): User =
        User(
            id = row[Users.id],
            name = row[Users.name],
            password = row[Users.password],
            salt = row[Users.salt],
            dateUpdated = row[Users.dateUpdated]
        )
}