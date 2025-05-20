package br.usp.eventUSP.model

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
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
    var categoria: String,
    var organizador: UsuarioOrganizador
) {
    var participantesInteressados: MutableList<UsuarioParticipante> = mutableListOf()
    var numeroLikes: Int = 0
    var reviews: MutableList<Review> = mutableListOf()
    var imagens: MutableList<ImagemEvento> = mutableListOf()
    
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
     * Verifica se já se passaram pelo menos 48 horas desde o fim do evento
     * @return true se já se passaram pelo menos 48 horas, false caso contrário
     */
    fun passaram48HorasDesdeOFim(): Boolean {
        if (!jaOcorreu()) return false
        val horasPassadas = ChronoUnit.HOURS.between(dataHora, LocalDateTime.now())
        return horasPassadas >= 48
    }
    
    /**
     * Adiciona uma review ao evento
     * @param participante O usuário participante que está adicionando a review
     * @param nota A nota (de 0 a 5) dada ao evento
     * @param comentario O comentário sobre o evento
     * @return A review criada ou null se não foi possível criar
     */
    fun adicionarReview(participante: UsuarioParticipante, nota: Int, comentario: String): Review? {
        // Verifica se o participante está na lista de interessados
        if (!participantesInteressados.contains(participante)) return null
        
        // Verifica se já se passaram 48 horas desde o fim do evento
        if (!passaram48HorasDesdeOFim()) return null
        
        // Verifica se o participante já fez uma review
        if (reviews.any { it.participante == participante }) return null
        
        val review = Review(
            evento = this,
            participante = participante,
            nota = nota,
            comentario = comentario
        )
        
        reviews.add(review)
        return review
    }
    
    /**
     * Calcula a média das notas das reviews
     * @return A média das notas ou null se não houver reviews
     */
    fun mediaDasReviews(): Double? {
        if (reviews.isEmpty()) return null
        return reviews.map { it.nota.toDouble() }.average()
    }
    
    /**
     * Adiciona uma imagem ao evento
     * @param url URL da imagem
     * @param descricao Descrição opcional da imagem
     * @param ordem Ordem de exibição da imagem
     * @return A imagem adicionada
     */
    fun adicionarImagem(url: String, descricao: String? = null, ordem: Int = imagens.size): ImagemEvento {
        val imagem = ImagemEvento(
            evento = this,
            url = url,
            descricao = descricao,
            ordem = ordem
        )
        imagens.add(imagem)
        return imagem
    }
    
    /**
     * Remove uma imagem do evento
     * @param imagem A imagem a ser removida
     * @return true se a imagem foi removida com sucesso, false caso contrário
     */
    fun removerImagem(imagem: ImagemEvento): Boolean {
        return imagens.remove(imagem)
    }
    
    /**
     * Obtém as imagens ordenadas
     * @return Lista de imagens ordenadas pela propriedade ordem
     */
    fun obterImagensOrdenadas(): List<ImagemEvento> {
        return imagens.sortedBy { it.ordem }
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
