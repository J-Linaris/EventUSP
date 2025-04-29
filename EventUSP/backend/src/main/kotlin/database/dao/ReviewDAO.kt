package br.usp.eventUSP.database.dao

import br.usp.eventUSP.database.tables.ReviewTable
import br.usp.eventUSP.model.Review
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

/**
 * DAO para a entidade Review
 */
class ReviewDAO(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<ReviewDAO>(ReviewTable)
    
    var evento by EventoDAO referencedOn ReviewTable.eventoId
    var participante by UsuarioParticipanteDAO referencedOn ReviewTable.participanteId
    var nota by ReviewTable.nota
    var comentario by ReviewTable.comentario
    
    /**
     * Converte o DAO para o modelo
     */
    fun toModel(): Review {
        return Review(
            id = id.value,
            evento = evento.toModel(),
            participante = participante.toModel(),
            nota = nota,
            comentario = comentario
        )
    }
}