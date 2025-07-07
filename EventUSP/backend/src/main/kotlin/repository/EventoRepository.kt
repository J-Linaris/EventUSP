package br.usp.eventUSP.repository

import br.usp.eventUSP.database.dao.EventoDAO
import br.usp.eventUSP.database.dao.ImagemEventoDAO
import br.usp.eventUSP.database.dao.UsuarioOrganizadorDAO
import br.usp.eventUSP.database.dao.UsuarioParticipanteDAO
import org.jetbrains.exposed.sql.insertIgnore
import br.usp.eventUSP.database.tables.ParticipantesInteressadosTable
import br.usp.eventUSP.model.Evento
import br.usp.eventUSP.model.ImagemEvento
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

/**
 * Repositório para operações relacionadas a Eventos
 */
class EventoRepository {
    /**
     * Cria um novo evento no banco de dados
     * @param evento O evento a ser criado
     * @return O evento criado com o ID gerado
     */
    fun create(evento: Evento): Evento = transaction {
        val organizadorDAO = UsuarioOrganizadorDAO.findById(evento.organizador.id!!)
            ?: throw IllegalArgumentException("Organizador não encontrado")

        val eventoDAO = EventoDAO.new {
            titulo = evento.titulo
            descricao = evento.descricao
            dataHora = evento.dataHora
            localizacao = evento.localizacao
            categoria = evento.categoria
            organizador = organizadorDAO
            numeroLikes = evento.numeroLikes
        }

        // Adiciona os relacionamentos
        evento.participantesInteressados.forEach { participante ->
            val participanteDAO = UsuarioParticipanteDAO.findById(participante.id!!)
            if (participanteDAO != null) {
                ParticipantesInteressadosTable.insertIgnore {
                    it[eventoId] = eventoDAO.id
                    it[participanteId] = participanteDAO.id
                }
            }
        }

        // Adiciona as imagens do evento
        evento.imagens.forEach { imagem ->
            ImagemEventoDAO.new {
                eventoId = eventoDAO.id
                url = imagem.url
                descricao = imagem.descricao
                ordem = imagem.ordem
            }
        }

        // Retorna o evento criado
        eventoDAO.toModel()
    }

    /**
     * Busca um evento pelo ID
     * @param id O ID do evento
     * @return O evento encontrado ou null se não existir
     */
    fun findById(id: Long): Evento? = transaction {
        EventoDAO.findById(id)?.toModel()
    }

    /**
     * Busca todos os eventos
     * @return Lista de todos os eventos
     */
    fun findAll(): List<Evento> = transaction {
        EventoDAO.all().map { it.toModel() }
    }

    /**
     * Busca eventos por categoria
     * @param categoria A categoria dos eventos
     * @return Lista de eventos da categoria especificada
     */
    fun findByCategoria(categoria: String): List<Evento> = transaction {
        EventoDAO.find { br.usp.eventUSP.database.tables.EventoTable.categoria eq categoria }
            .map { it.toModel() }
    }

    /**
     * Busca eventos futuros (a partir da data atual)
     * @return Lista de eventos futuros
     */
    fun findFuturos(): List<Evento> = transaction {
        EventoDAO.find { br.usp.eventUSP.database.tables.EventoTable.dataHora greaterEq LocalDateTime.now() }
            .map { it.toModel() }
    }

    /**
     * Atualiza um evento existente
     * @param evento O evento com as informações atualizadas
     * @return O evento atualizado
     */
    fun update(evento: Evento): Evento = transaction {
        val eventoDAO = EventoDAO.findById(evento.id!!)
            ?: throw IllegalArgumentException("Evento não encontrado")

        eventoDAO.titulo = evento.titulo
        eventoDAO.descricao = evento.descricao
        // COMENTA-SE A LINHA ABAIXO APENAS PARA UM TESTE
        eventoDAO.dataHora = evento.dataHora
        eventoDAO.localizacao = evento.localizacao
        eventoDAO.categoria = evento.categoria
        eventoDAO.numeroLikes = evento.numeroLikes

        // Atualiza os relacionamentos se necessário

        eventoDAO.toModel()
    }

    /**
     * Remove um evento pelo ID
     * @param id O ID do evento a ser removido
     * @return true se o evento foi removido, false caso contrário
     */
    fun delete(id: Long): Boolean = transaction {
        val eventoDAO = EventoDAO.findById(id) ?: return@transaction false
        eventoDAO.delete()
        true
    }
}
