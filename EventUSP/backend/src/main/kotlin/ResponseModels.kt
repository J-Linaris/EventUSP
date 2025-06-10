package br.usp.eventUSP

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse<T>(
    val message: String,
    val user: T,
    val token: String
)
