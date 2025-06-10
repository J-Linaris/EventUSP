package br.usp.eventUSP.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

/**
 * Classe que representa uma avaliação (review) de um evento
 */
@Serializable
class Review(
    var id: Long? = null,
    val evento: Evento,
    val participante: UsuarioParticipante,
    var nota: Int, // valor de 0 a 5
    var comentario: String,
    @Contextual val dataHora: LocalDateTime = LocalDateTime.now()
) {
    init {
        require(nota in 0..5) { "A nota deve estar entre 0 e 5" }
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Review) return false
        
        if (id != null && other.id != null) {
            return id == other.id
        }
        
        return evento == other.evento && participante == other.participante
    }
    
    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        if (result == 0) {
            result = 31 * result + evento.hashCode()
            result = 31 * result + participante.hashCode()
        }
        return result
    }
}
