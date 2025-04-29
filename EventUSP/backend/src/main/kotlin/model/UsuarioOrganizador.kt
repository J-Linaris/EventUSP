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
    var telefone: String? = null
) : UsuarioParticipante(id, nome, email, senha) {
    
    var eventosOrganizados: MutableList<Evento> = mutableListOf()
    
    /**
     * Cria um novo evento organizado por este usuário
     * @param titulo Título do evento
     * @param descricao Descrição do evento
     * @param dataHora Data e hora do evento
     * @param localizacao Localização do evento
     * @param imagem URL da imagem do evento
     * @param categoria Categoria do evento
     * @param capacidadeMaxima Capacidade máxima de participantes
     * @return O evento criado
     */
    fun criarEvento(
        titulo: String,
        descricao: String,
        dataHora: java.time.LocalDateTime,
        localizacao: String,
        imagem: String,
        categoria: String,
        capacidadeMaxima: Int
    ): Evento {
        val evento = Evento(
            titulo = titulo,
            descricao = descricao,
            dataHora = dataHora,
            localizacao = localizacao,
            imagem = imagem,
            categoria = categoria,
            capacidadeMaxima = capacidadeMaxima,
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
     * @param imagem Nova URL de imagem (opcional)
     * @param categoria Nova categoria (opcional)
     * @param capacidadeMaxima Nova capacidade máxima (opcional)
     * @return true se o evento foi atualizado com sucesso, false caso contrário
     */
    fun atualizarEvento(
        evento: Evento,
        titulo: String? = null,
        descricao: String? = null,
        dataHora: java.time.LocalDateTime? = null,
        localizacao: String? = null,
        imagem: String? = null,
        categoria: String? = null,
        capacidadeMaxima: Int? = null
    ): Boolean {
        // Verifica se o evento pertence a este organizador
        if (evento.organizador != this) return false
        
        // Atualiza os campos não nulos
        titulo?.let { evento.titulo = it }
        descricao?.let { evento.descricao = it }
        dataHora?.let { evento.dataHora = it }
        localizacao?.let { evento.localizacao = it }
        imagem?.let { evento.imagem = it }
        categoria?.let { evento.categoria = it }
        capacidadeMaxima?.let { evento.capacidadeMaxima = it }
        
        return true
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
