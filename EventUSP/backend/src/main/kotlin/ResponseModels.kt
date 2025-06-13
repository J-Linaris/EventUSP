package br.usp.eventUSP

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse<T>(
    val message: String,
    val user: T,
    val token: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val senha: String
)

@Serializable
data class LoginResponse<T>(
    val message: String,
    val user: T
)
