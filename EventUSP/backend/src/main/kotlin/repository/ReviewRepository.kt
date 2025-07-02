package br.usp.eventUSP.repository

import br.usp.eventUSP.database.dao.EventoDAO
import br.usp.eventUSP.database.dao.ReviewDAO
import br.usp.eventUSP.database.dao.UsuarioParticipanteDAO
import br.usp.eventUSP.model.Review
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Repositório para operações relacionadas a Reviews
 */
class ReviewRepository {
    /**
     * Cria uma nova review no banco de dados
     * @param review A review a ser criada
     * @return A review criada com o ID gerado
     */
    fun create(review: Review): Review = transaction {
        val eventoDAO = EventoDAO.findById(review.eventoId)
            ?: throw IllegalArgumentException("Evento não encontrado")
            
        val participanteDAO = UsuarioParticipanteDAO.findById(review.participanteId)
            ?: throw IllegalArgumentException("Participante não encontrado")
            
        val reviewDAO = ReviewDAO.new {
            eventoId = eventoDAO.id
            participanteId = participanteDAO.id
            nota = review.nota
            comentario = review.comentario
        }
        
        reviewDAO.toModel()
    }
    
    /**
     * Busca uma review pelo ID
     * @param id O ID da review
     * @return A review encontrada ou null se não existir
     */
    fun findById(id: Long): Review? = transaction {
        ReviewDAO.findById(id)?.toModel()
    }
    
    /**
     * Busca todas as reviews de um evento
     * @param eventoId O ID do evento
     * @return Lista de reviews do evento
     */
    fun findByEvento(eventoId: Long): List<Review> = transaction {
        ReviewDAO.find { br.usp.eventUSP.database.tables.ReviewTable.eventoId eq eventoId }
            .map { it.toModel() }
    }
    
    /**
     * Busca todas as reviews de um participante
     * @param participanteId O ID do participante
     * @return Lista de reviews do participante
     */
    fun findByParticipante(participanteId: Long): List<Review> = transaction {
        ReviewDAO.find { br.usp.eventUSP.database.tables.ReviewTable.participanteId eq participanteId }
            .map { it.toModel() }
    }
    
    /**
     * Busca todas as reviews
     * @return Lista de todas as reviews
     */
    fun findAll(): List<Review> = transaction {
        ReviewDAO.all().map { it.toModel() }
    }
    
    /**
     * Atualiza uma review existente
     * @param review A review com as informações atualizadas
     * @return A review atualizada
     */
    fun update(review: Review): Review = transaction {
        val reviewDAO = ReviewDAO.findById(review.id!!)
            ?: throw IllegalArgumentException("Review não encontrada")
            
        reviewDAO.nota = review.nota
        reviewDAO.comentario = review.comentario
        
        reviewDAO.toModel()
    }
    
    /**
     * Remove uma review pelo ID
     * @param id O ID da review a ser removida
     * @return true se a review foi removida, false caso contrário
     */
    fun delete(id: Long): Boolean = transaction {
        val reviewDAO = ReviewDAO.findById(id) ?: return@transaction false
        reviewDAO.delete()
        true
    }
}