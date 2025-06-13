package br.usp.eventUSP.repository

import br.usp.eventUSP.database.dao.UsuarioOrganizadorDAO
import br.usp.eventUSP.model.UsuarioOrganizador
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Repositório para operações relacionadas a Usuários Organizadores
 */
class UsuarioOrganizadorRepository {
    /**
     * Cria um novo usuário organizador no banco de dados
     * @param usuario O usuário organizador a ser criado
     * @return O usuário organizador criado com o ID gerado
     */
    fun create(usuario: UsuarioOrganizador): UsuarioOrganizador = transaction {
        val usuarioDAO = UsuarioOrganizadorDAO.new {
            nome = usuario.nome
            email = usuario.email
            senha = usuario.senha
            fotoPerfil = usuario.fotoPerfil
        }
        
        usuarioDAO.toModel()
    }
    
    /**
     * Busca um usuário organizador pelo ID
     * @param id O ID do usuário organizador
     * @return O usuário organizador encontrado ou null se não existir
     */
    fun findById(id: Long): UsuarioOrganizador? = transaction {
        UsuarioOrganizadorDAO.findById(id)?.toModel()
    }
    
    /**
     * Busca um usuário organizador pelo email
     * @param email O email do usuário organizador
     * @return O usuário organizador encontrado ou null se não existir
     */
    fun findByEmail(email: String): UsuarioOrganizador? = transaction {
        UsuarioOrganizadorDAO.find { br.usp.eventUSP.database.tables.UsuarioOrganizadorTable.email eq email }
            .firstOrNull()?.toModel()
    }
    
    /**
     * Busca todos os usuários organizadores
     * @return Lista de todos os usuários organizadores
     */
    fun findAll(): List<UsuarioOrganizador> = transaction {
        UsuarioOrganizadorDAO.all().map { it.toModel() }
    }
    
    /**
     * Atualiza um usuário organizador existente
     * @param usuario O usuário organizador com as informações atualizadas
     * @return O usuário organizador atualizado
     */
    fun update(usuario: UsuarioOrganizador): UsuarioOrganizador = transaction {
        val usuarioDAO = UsuarioOrganizadorDAO.findById(usuario.id!!)
            ?: throw IllegalArgumentException("Usuário organizador não encontrado")
            
        usuarioDAO.nome = usuario.nome
        usuarioDAO.email = usuario.email
        usuarioDAO.senha = usuario.senha
        usuarioDAO.fotoPerfil = usuario.fotoPerfil
        
        usuarioDAO.toModel()
    }
    
    /**
     * Remove um usuário organizador pelo ID
     * @param id O ID do usuário organizador a ser removido
     * @return true se o usuário organizador foi removido, false caso contrário
     */
    fun delete(id: Long): Boolean = transaction {
        val usuarioDAO = UsuarioOrganizadorDAO.findById(id) ?: return@transaction false
        usuarioDAO.delete()
        true
    }
}