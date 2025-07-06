package br.usp.eventUSP.database.dao

import br.usp.eventUSP.database.tables.*
import br.usp.eventUSP.model.Evento
import br.usp.eventUSP.model.ImagemEvento
import br.usp.eventUSP.model.ParticipanteDTO
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
    var categoria by EventoTable.categoria
    var organizador by UsuarioOrganizadorDAO referencedOn EventoTable.organizadorId
    var numeroLikes by EventoTable.numeroLikes
    
    // Relacionamentos
    val participantesInteressados by UsuarioParticipanteDAO via ParticipantesInteressadosTable
    val reviews by ReviewDAO referrersOn ReviewTable.eventoId
    val imagens by ImagemEventoDAO referrersOn ImagemEventoTable.eventoId

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
//            numeroLikes = this.numeroLikes,
            categoria = categoria,
            organizador = organizador.toModel(),
            // Carrega os relacionamentos
//            participantesInteressados = this.participantesInteressados.map { it.toModel() }.toMutableList(),
//            reviews = this.reviews.map { it.toModel() }.toMutableList(),
//            imagens = this.imagens.map { it.toModel() }.toMutableList()
        )

        // ALTERADO: Agora mapeamos para o DTO simplificado, que não tem recursão.
        evento.participantesInteressados = participantesInteressados.map { participanteDAO ->
            ParticipanteDTO(
                id = participanteDAO.id.value,
                nome = participanteDAO.nome
//                fotoPerfil = participanteDAO.fotoPerfil // Adicione a foto de perfil se não tiver no DAO
            )
        }.toMutableList()

        evento.reviews = reviews.map { it.toModel() }.toMutableList()
        evento.imagens = imagens.map { it.toModel() }.toMutableList()
        evento.numeroLikes = this.numeroLikes
        

        
        return evento
    }
}