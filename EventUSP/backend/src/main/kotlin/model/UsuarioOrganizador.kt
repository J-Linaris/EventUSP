package br.usp.eventUSP.model

/**
 * Classe que representa um usuário organizador no sistema EventUSP
 * Herda de UsuarioParticipante e adiciona funcionalidades específicas para organização de eventos
 */
class UsuarioOrganizador(
    id: Long? = null,
    nome: String,
    email: String,
    senha: String,
    var cpf: String,
    var instituicao: String? = null,
    var telefone: String? = null,
    fotoPerfil: String? = null
) : UsuarioParticipante(id, nome, email, senha, fotoPerfil) {
    
    var eventosOrganizados: MutableList<Evento> = mutableListOf()
    
    /**
     * Cria um novo evento organizado por este usuário
     * @param titulo Título do evento
     * @param descricao Descrição do evento
     * @param dataHora Data e hora do evento
     * @param localizacao Localização do evento
     * @param imagemCapa URL da imagem de capa do evento
     * @param categoria Categoria do evento
     * @return O evento criado
     */
    fun criarEvento(
        titulo: String,
        descricao: String,
        dataHora: java.time.LocalDateTime,
        localizacao: String,
        imagemCapa: String,
        categoria: String
    ): Evento {
        val evento = Evento(
            titulo = titulo,
            descricao = descricao,
            dataHora = dataHora,
            localizacao = localizacao,
            imagemCapa = imagemCapa,
            categoria = categoria,
            organizador = this
        )
        eventosOrganizados.add(evento)
        return evento
    }
    
    /**
     * Cancela um evento organizado por este usuário
     * @param evento O evento a ser cancelado
     * @return true se o evento foi cancelado com sucesso, false caso contrário
     */
    fun cancelarEvento(evento: Evento): Boolean {
        // Verifica se o evento pertence a este organizador
        if (evento.organizador != this) return false
        
        return eventosOrganizados.remove(evento)
    }
    
    /**
     * Atualiza informações de um evento
     * @param evento O evento a ser atualizado
     * @param titulo Novo título (opcional)
     * @param descricao Nova descrição (opcional)
     * @param dataHora Nova data e hora (opcional)
     * @param localizacao Nova localização (opcional)
     * @param imagemCapa Nova URL de imagem de capa (opcional)
     * @param categoria Nova categoria (opcional)
     * @return true se o evento foi atualizado com sucesso, false caso contrário
     */
    fun atualizarEvento(
        evento: Evento,
        titulo: String? = null,
        descricao: String? = null,
        dataHora: java.time.LocalDateTime? = null,
        localizacao: String? = null,
        imagemCapa: String? = null,
        categoria: String? = null
    ): Boolean {
        // Verifica se o evento pertence a este organizador
        if (evento.organizador != this) return false
        
        // Atualiza os campos não nulos
        titulo?.let { evento.titulo = it }
        descricao?.let { evento.descricao = it }
        dataHora?.let { evento.dataHora = it }
        localizacao?.let { evento.localizacao = it }
        imagemCapa?.let { evento.imagemCapa = it }
        categoria?.let { evento.categoria = it }
        
        return true
    }
    
    /**
     * Adiciona uma imagem adicional a um evento
     * @param evento O evento a receber a imagem
     * @param url URL da imagem
     * @param descricao Descrição opcional da imagem
     * @param ordem Ordem de exibição da imagem
     * @return A imagem adicionada ou null se o evento não pertencer a este organizador
     */
    fun adicionarImagemAoEvento(
        evento: Evento,
        url: String,
        descricao: String? = null,
        ordem: Int = evento.imagensAdicionais.size
    ): ImagemEvento? {
        // Verifica se o evento pertence a este organizador
        if (evento.organizador != this) return null
        
        return evento.adicionarImagem(url, descricao, ordem)
    }
    
    /**
     * Remove uma imagem adicional de um evento
     * @param evento O evento que contém a imagem
     * @param imagem A imagem a ser removida
     * @return true se a imagem foi removida com sucesso, false caso contrário
     */
    fun removerImagemDoEvento(evento: Evento, imagem: ImagemEvento): Boolean {
        // Verifica se o evento pertence a este organizador
        if (evento.organizador != this) return false
        
        return evento.removerImagem(imagem)
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UsuarioOrganizador) return false
        if (!super.equals(other)) return false
        
        return cpf == other.cpf
    }
    
    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + cpf.hashCode()
        return result
    }
}
