package com.devlhse.model

import org.jetbrains.exposed.sql.Table

object Users : Table("tb_user") {
    val id = integer("id").autoIncrement().primaryKey()
    val name = varchar("name", 255)
    val password = varchar("password", 255)
    val salt = varchar("salt", 255)
    val dateUpdated = long("dateUpdated")
}

class User(val id: Int?, val name: String, val password: String, val salt: String?, val dateUpdated: Long?)