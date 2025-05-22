package br.usp.eventUSP

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*
import java.util.concurrent.TimeUnit

// Constantes para configuração do JWT
private const val JWT_SECRET = "EventUSP_Secret_Key" // Em produção, use variáveis de ambiente
private const val ISSUER = "event-usp-app"
private const val AUDIENCE = "event-usp-users"
private val JWT_EXPIRATION = TimeUnit.DAYS.toMillis(7) // Token válido por 7 dias

fun Application.configureSecurity() {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = "EventUSP Server"
            verifier(
                JWT.require(Algorithm.HMAC256(JWT_SECRET))
                    .withAudience(AUDIENCE)
                    .withIssuer(ISSUER)
                    .build()
            )
            validate { credential ->
                // Validar claims e retornar JWTPrincipal se válido
                if (credential.payload.audience.contains(AUDIENCE)) {
                    JWTPrincipal(credential.payload)
                } else null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token de autenticação inválido ou expirado")
            }
        }
    }
}

/**
 * Gera um token JWT para autenticação do usuário
 * 
 * @param userId ID do usuário
 * @param role Papel do usuário (organizador ou participante)
 * @return Token JWT gerado
 */
fun generateToken(userId: String, role: String): String {
    return JWT.create()
        .withAudience(AUDIENCE)
        .withIssuer(ISSUER)
        .withClaim("userId", userId)
        .withClaim("role", role)
        .withExpiresAt(Date(System.currentTimeMillis() + JWT_EXPIRATION))
        .sign(Algorithm.HMAC256(JWT_SECRET))
}