package br.usp.eventUSP.database.dao

import br.usp.eventUSP.database.tables.ParticipantesInteressadosTable
import br.usp.eventUSP.database.tables.ReviewTable
import br.usp.eventUSP.database.tables.UsuarioParticipanteTable
import br.usp.eventUSP.model.EventoDTO
import br.usp.eventUSP.model.UsuarioParticipante
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import kotlinx.serialization.Serializable

/**
 * DAO para a entidade UsuarioParticipante
 */

// Cria um data class para UsuarioParticipante ser serializável (transformado entre nosso modelo e um JSON)


class UsuarioParticipanteDAO(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UsuarioParticipanteDAO>(UsuarioParticipanteTable)
    
    var nome by UsuarioParticipanteTable.nome
    var email by UsuarioParticipanteTable.email
    var senha by UsuarioParticipanteTable.senha
//    var fotoPerfil by UsuarioParticipanteTable.fotoPerfil
    
    // Relacionamentos
    val eventosInteressados by EventoDAO via ParticipantesInteressadosTable
    val reviews by ReviewDAO referrersOn ReviewTable.participanteId
    
    /**
     * Converte o DAO para o modelo
     */
    fun toModel(): UsuarioParticipante {
        val usuario = UsuarioParticipante(
            id = id.value,
            nome = nome,
            email = email,
            senha = senha
        )
        usuario.eventosInteressados = this.eventosInteressados.map { eventoDAO ->
            EventoDTO(
                id = eventoDAO.id.value,
                titulo = eventoDAO.titulo,
                dataHora = eventoDAO.dataHora.toString(),
                localizacao = eventoDAO.localizacao,
                categoria = eventoDAO.categoria
            )
        }.toMutableList()
        usuario.eventosInteressadosIds = this.eventosInteressados.map { it.id.value }.toMutableList()
        usuario.reviewsFeitas = reviews.map { it.toModel() }.toMutableList()
        return usuario
    }
}