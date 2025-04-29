package br.usp.eventUSP.model

/**
 * Classe que representa um usuário participante no sistema EventUSP
 */
open class UsuarioParticipante(
    var id: Long? = null,
    var nome: String,
    var email: String,
    var senha: String,
    var fotoPerfil: String? = null
) {
    var eventosComLike: MutableList<Evento> = mutableListOf()
    var eventosInteressado: MutableList<Evento> = mutableListOf()
    var reviewsFeitas: MutableList<Review> = mutableListOf()
    
    /**
     * Adiciona um like em um evento
     * @param evento O evento a receber o like
     * @return true se o like foi adicionado com sucesso, false caso já tenha dado like no evento
     */
    fun darLike(evento: Evento): Boolean {
        if (eventosComLike.contains(evento)) return false
        eventosComLike.add(evento)
        evento.adicionarLike()
        return true
    }
    
    /**
     * Remove um like de um evento
     * @param evento O evento a ter o like removido
     * @return true se o like foi removido com sucesso, false caso não tenha dado like no evento
     */
    fun removerLike(evento: Evento): Boolean {
        if (!eventosComLike.contains(evento)) return false
        eventosComLike.remove(evento)
        evento.removerLike()
        return true
    }
    
    /**
     * Demonstra interesse em participar de um evento
     * @param evento O evento que o usuário tem interesse
     * @return true se o interesse foi registrado com sucesso, false caso já tenha demonstrado interesse
     */
    fun demonstrarInteresse(evento: Evento): Boolean {
        if (eventosInteressado.contains(evento)) return false
        eventosInteressado.add(evento)
        evento.adicionarParticipanteInteressado(this)
        return true
    }
    
    /**
     * Remove o interesse em participar de um evento
     * @param evento O evento que o usuário não tem mais interesse
     * @return true se o interesse foi removido com sucesso, false caso não tenha demonstrado interesse
     */
    fun removerInteresse(evento: Evento): Boolean {
        if (!eventosInteressado.contains(evento)) return false
        eventosInteressado.remove(evento)
        evento.removerParticipanteInteressado(this)
        return true
    }
    
    /**
     * Adiciona uma review para um evento
     * @param evento O evento a ser avaliado
     * @param nota A nota (de 0 a 5) dada ao evento
     * @param comentario O comentário sobre o evento
     * @return A review criada ou null se não foi possível criar
     */
    fun adicionarReview(evento: Evento, nota: Int, comentario: String): Review? {
        // A verificação se o participante está interessado e se passaram 48h é feita no Evento
        val review = evento.adicionarReview(this, nota, comentario) ?: return null
        
        reviewsFeitas.add(review)
        return review
    }
    
    /**
     * Verifica se já fez uma review para o evento
     * @param evento O evento a verificar
     * @return true se já fez review, false caso contrário
     */
    fun jaFezReviewParaEvento(evento: Evento): Boolean {
        return reviewsFeitas.any { it.evento == evento }
    }
    
    /**
     * Atualiza a foto de perfil do usuário
     * @param novaFoto URL da nova foto de perfil
     */
    fun atualizarFotoPerfil(novaFoto: String) {
        this.fotoPerfil = novaFoto
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UsuarioParticipante) return false
        
        return id == other.id || email == other.email
    }
    
    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + email.hashCode()
        return result
    }
}
