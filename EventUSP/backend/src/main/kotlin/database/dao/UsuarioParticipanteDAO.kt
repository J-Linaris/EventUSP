package br.usp.eventUSP.database.dao

import br.usp.eventUSP.database.tables.ParticipantesInteressadosTable
import br.usp.eventUSP.database.tables.ReviewTable
import br.usp.eventUSP.database.tables.UsuarioParticipanteTable
import br.usp.eventUSP.model.UsuarioParticipante
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

/**
 * DAO para a entidade UsuarioParticipante
 */
class UsuarioParticipanteDAO(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UsuarioParticipanteDAO>(UsuarioParticipanteTable)
    
    var nome by UsuarioParticipanteTable.nome
    var email by UsuarioParticipanteTable.email
    var senha by UsuarioParticipanteTable.senha
    
    // Relacionamentos
    val eventosInteressados by EventoDAO via ParticipantesInteressadosTable
    val reviews by ReviewDAO referrersOn ReviewTable.participanteId
    
    /**
     * Converte o DAO para o modelo
     */
    fun toModel(): UsuarioParticipante {
        return UsuarioParticipante(
            id = id.value,
            nome = nome,
            email = email,
            senha = senha
        )
    }
}