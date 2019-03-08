package com.devlhse.model

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

open class SimpleJWT(val secret: String) {
    private val validityInMs = 36_000_00 * 1 // 1 hour
    private val algorithm = Algorithm.HMAC256(secret)
    val verifier = JWT.require(algorithm).build()
    fun sign(name: String): String = JWT.create().withClaim("name", name).withExpiresAt(getExpiration()).sign(algorithm)
    private fun getExpiration() = Date(System.currentTimeMillis() + validityInMs)
}