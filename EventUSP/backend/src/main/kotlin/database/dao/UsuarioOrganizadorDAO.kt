package br.usp.eventUSP.database.dao

import br.usp.eventUSP.database.tables.EventoTable
import br.usp.eventUSP.database.tables.UsuarioOrganizadorTable
import br.usp.eventUSP.model.UsuarioOrganizador
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

/**
 * DAO para a entidade UsuarioOrganizador
 */
class UsuarioOrganizadorDAO(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UsuarioOrganizadorDAO>(UsuarioOrganizadorTable)
    
    var nome by UsuarioOrganizadorTable.nome
    var email by UsuarioOrganizadorTable.email
    var senha by UsuarioOrganizadorTable.senha
    var fotoPerfil by UsuarioOrganizadorTable.fotoPerfil
    
    // Relacionamentos
    val eventos by EventoDAO referrersOn EventoTable.organizadorId
    
    /**
     * Converte o DAO para o modelo
     */
    fun toModel(): UsuarioOrganizador {
        val org = UsuarioOrganizador()
        org.id = id.value
        org.nome = nome
        org.email = email
        org.senha = senha
        org.fotoPerfil = fotoPerfil
        return org
    }
}