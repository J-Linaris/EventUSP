package br.usp.eventUSP.repository

import br.usp.eventUSP.database.dao.UsuarioParticipanteDAO
import br.usp.eventUSP.model.UsuarioParticipante
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Repositório para operações relacionadas a Usuários Participantes
 */
class UsuarioParticipanteRepository {
    /**
     * Cria um usuário participante no banco de dados
     * @param usuario O usuário participante a ser criado
     * @return O usuário participante criado com o ID gerado
     */
    fun create(usuario: UsuarioParticipante): UsuarioParticipante = transaction {
        val usuarioDAO = UsuarioParticipanteDAO.new {
            nome = usuario.nome
            email = usuario.email
            senha = usuario.senha
        }
        
        usuarioDAO.toModel()
    }
    
    /**
     * Busca um usuário participante pelo ID
     * @param id O ID do usuário participante
     * @return O usuário participante encontrado ou null se não existir
     */
    fun findById(id: Long): UsuarioParticipante? = transaction {
        UsuarioParticipanteDAO.findById(id)?.toModel()
    }
    
    /**
     * Busca um usuário participante pelo email
     * @param email O email do usuário participante
     * @return O usuário participante encontrado ou null se não existir
     */
    fun findByEmail(email: String): UsuarioParticipante? = transaction {
        UsuarioParticipanteDAO.find { br.usp.eventUSP.database.tables.UsuarioParticipanteTable.email eq email }
            .firstOrNull()?.toModel()
    }
    
    /**
     * Busca todos os usuários participantes
     * @return Lista de todos os usuários participantes
     */
    fun findAll(): List<UsuarioParticipante> = transaction {
        UsuarioParticipanteDAO.all().map { it.toModel() }
    }
    
    /**
     * Atualiza um usuário participante existente
     * @param usuario O usuário participante com as informações atualizadas
     * @return O usuário participante atualizado
     */
    fun update(usuario: UsuarioParticipante): UsuarioParticipante = transaction {
        val usuarioDAO = UsuarioParticipanteDAO.findById(usuario.id!!)
            ?: throw IllegalArgumentException("Usuário participante não encontrado")
            
        usuarioDAO.nome = usuario.nome
        usuarioDAO.email = usuario.email
        usuarioDAO.senha = usuario.senha
        
        usuarioDAO.toModel()
    }
    
    /**
     * Remove um usuário participante pelo ID
     * @param id O ID do usuário participante a ser removido
     * @return true se o usuário participante foi removido, false caso contrário
     */
    fun delete(id: Long): Boolean = transaction {
        val usuarioDAO = UsuarioParticipanteDAO.findById(id) ?: return@transaction false
        usuarioDAO.delete()
        true
    }
}