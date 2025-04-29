package br.usp.eventUSP.database.dao

import br.usp.eventUSP.database.tables.*
import br.usp.eventUSP.model.Evento
import br.usp.eventUSP.model.ImagemEvento
import br.usp.eventUSP.model.Review
import br.usp.eventUSP.model.UsuarioParticipante
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

/**
 * DAO para a entidade Evento
 */
class EventoDAO(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<EventoDAO>(EventoTable)
    
    var titulo by EventoTable.titulo
    var descricao by EventoTable.descricao
    var dataHora by EventoTable.dataHora
    var localizacao by EventoTable.localizacao
    var imagemCapa by EventoTable.imagemCapa
    var categoria by EventoTable.categoria
    var organizador by UsuarioOrganizadorDAO referencedOn EventoTable.organizadorId
    var numeroLikes by EventoTable.numeroLikes
    
    // Relacionamentos
    val participantesInteressados by UsuarioParticipanteDAO via ParticipantesInteressadosTable
    val reviews by ReviewDAO referrersOn ReviewTable.eventoId
    val imagensAdicionais by ImagemEventoDAO referrersOn ImagemEventoTable.eventoId
    
    /**
     * Converte o DAO para o modelo
     */
    fun toModel(): Evento {
        val evento = Evento(
            id = id.value,
            titulo = titulo,
            descricao = descricao,
            dataHora = dataHora,
            localizacao = localizacao,
            imagemCapa = imagemCapa,
            categoria = categoria,
            organizador = organizador.toModel()
        )
        
        // Carrega os relacionamentos
        evento.numeroLikes = numeroLikes
        evento.participantesInteressados = participantesInteressados.map { it.toModel() }.toMutableList()
        evento.reviews = reviews.map { it.toModel() }.toMutableList()
        evento.imagensAdicionais = imagensAdicionais.map { it.toModel() }.toMutableList()
        
        return evento
    }
}