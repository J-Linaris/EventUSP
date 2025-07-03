package br.usp.eventUSP.database.dao

import br.usp.eventUSP.database.tables.ImagemEventoTable
import br.usp.eventUSP.model.ImagemEvento
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

/**
 * DAO para a entidade ImagemEvento
 */
class ImagemEventoDAO(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<ImagemEventoDAO>(ImagemEventoTable)
    
//    var evento by EventoDAO referencedOn ImagemEventoTable.eventoId
    var eventoId by ImagemEventoTable.eventoId
    var url by ImagemEventoTable.url
    var descricao by ImagemEventoTable.descricao
    var ordem by ImagemEventoTable.ordem
    
    /**
     * Converte o DAO para o modelo
     */
    fun toModel(): ImagemEvento {
        return ImagemEvento(
            id = id.value,
            eventoId = eventoId.value,
//            eventoId = evento.id.value,
            url = url,
            descricao = descricao,
            ordem = ordem
        )
    }
}