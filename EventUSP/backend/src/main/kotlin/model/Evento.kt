package br.usp.eventUSP.model

import java.time.LocalDateTime
import java.util.*

/**
 * Classe que representa um evento no sistema EventUSP
 */
class Evento(
    var id: Long? = null,
    var titulo: String,
    var descricao: String,
    var dataHora: LocalDateTime,
    var localizacao: String,
    var imagem: String,
    var categoria: String,
    var capacidadeMaxima: Int,
    var organizador: UsuarioOrganizador
) {
    var participantesInteressados: MutableList<UsuarioParticipante> = mutableListOf()
    var numeroLikes: Int = 0
    
    /**
     * Adiciona um participante interessado no evento
     * @param participante O usuário participante a ser adicionado
     * @return true se o participante foi adicionado com sucesso, false caso contrário
     */
    fun adicionarParticipanteInteressado(participante: UsuarioParticipante): Boolean {
        if (participantesInteressados.contains(participante)) return false
        return participantesInteressados.add(participante)
    }
    
    /**
     * Remove um participante da lista de interessados
     * @param participante O usuário participante a ser removido
     * @return true se o participante foi removido com sucesso, false caso contrário
     */
    fun removerParticipanteInteressado(participante: UsuarioParticipante): Boolean {
        return participantesInteressados.remove(participante)
    }
    
    /**
     * Incrementa o número de likes do evento
     */
    fun adicionarLike() {
        numeroLikes++
    }
    
    /**
     * Decrementa o número de likes do evento
     */
    fun removerLike() {
        if (numeroLikes > 0) numeroLikes--
    }
    
    /**
     * Verifica se o evento já ocorreu
     * @return true se o evento já ocorreu, false caso contrário
     */
    fun jaOcorreu(): Boolean {
        return dataHora.isBefore(LocalDateTime.now())
    }
    
    /**
     * Verifica se o evento está lotado
     * @return true se o evento está lotado, false caso contrário
     */
    fun estaLotado(): Boolean {
        return participantesInteressados.size >= capacidadeMaxima
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Evento) return false
        
        return id == other.id
    }
    
    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
