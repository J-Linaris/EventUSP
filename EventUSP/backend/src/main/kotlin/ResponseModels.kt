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
    val password: String
)

@Serializable
data class LoginResponse<T>(
    val message: String,
    val token: String,
    val user: T
)

@Serializable
data class EventoRequest(
    val titulo: String,
    val descricao: String,
    val dataHora: String, // Recebe como string (ISO-8601)
    val localizacao: String,
    val categoria: String,
    val organizadorId: Long
)

