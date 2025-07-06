package br.usp.eventUSP.model

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

/**
 * Representação simplificada de um Evento, para ser usada DENTRO de um objeto Participante.
 * Não contém a lista de participantes para evitar o loop.
 */
@Serializable
data class EventoDTO(
    val id: Long,
    val titulo: String,
    val dataHora: String, // Usando String para simplicidade na serialização
    val localizacao: String
)

/**
 * Representação simplificada de um Participante, para ser usada DENTRO de um objeto Evento.
 * Não contém a lista de eventos para evitar o loop.
 */
@Serializable
data class ParticipanteDTO(
    val id: Long,
    val nome: String
//    val fotoPerfil: String?
)
