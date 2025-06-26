package br.usp.eventUSP.model
import kotlinx.serialization.Serializable
/**
 * Classe que representa uma imagem de um evento
 */
@Serializable
class ImagemEvento(
    var id: Long? = null,
    var eventoId: Long,
    var url: String,
    var descricao: String? = null,
    var ordem: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ImagemEvento) return false
        
        if (id != null && other.id != null) {
            return id == other.id
        }
        
        return url == other.url
    }
    
    override fun hashCode(): Int {
        val result = id?.hashCode() ?: 0
        return result
    }
}
