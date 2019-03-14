package com.devlhse.model

import java.util.Base64

fun encrypt(passwordWithSalt: String): String {
    val bytes = passwordWithSalt.toByteArray()
    return Base64.getEncoder().encodeToString(bytes)
}
