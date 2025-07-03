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
    
    var eventoId by ReviewTable.eventoId
    var participanteId by ReviewTable.participanteId
    var nota by ReviewTable.nota
    var comentario by ReviewTable.comentario
    
    /**
     * Converte o DAO para o modelo
     */
    fun toModel(): Review {
        return Review(
            id = id.value,
            eventoId = eventoId.value,
            participanteId = participanteId.value,
            nota = nota,
            comentario = comentario
        )
    }
}