package com.devlhse.model

import org.jetbrains.exposed.sql.Table
import java.util.Base64

object Users : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val name = varchar("name", 255)
    val password = varchar("password", 255)
    val salt = varchar("salt", 255)
    val dateUpdated = long("dateUpdated")
}

class User(val id: Int?, val name: String, val password: String, val salt: String?, val dateUpdated: Long?)

fun encrypt(passwordWithSalt: String): String {
    val bytes = passwordWithSalt.toByteArray()
    return Base64.getEncoder().encodeToString(bytes)
}
