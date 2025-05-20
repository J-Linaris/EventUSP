package br.usp.eventUSP.model

/**
 * Classe que representa uma imagem de um evento
 */
class ImagemEvento(
    var id: Long? = null,
    var evento: Evento,
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
        
        return evento == other.evento && url == other.url
    }
    
    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        if (result == 0) {
            result = 31 * result + evento.hashCode()
            result = 31 * result + url.hashCode()
        }
        return result
    }
}
