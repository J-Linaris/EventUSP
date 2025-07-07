package br.usp.eventUSP.repository

import br.usp.eventUSP.database.dao.UsuarioParticipanteDAO
import br.usp.eventUSP.model.UsuarioParticipante
import org.jetbrains.exposed.sql.transactions.transaction
import br.usp.eventUSP.database.dao.EventoDAO
import br.usp.eventUSP.database.tables.EventoTable
import br.usp.eventUSP.database.tables.ParticipantesInteressadosTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

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

    // --- Adicionar Interesse (Dar Like) ---
    /**
     * Cria um registro de interesse (like) de um participante em um evento.
     * @param participanteId O ID do participante.
     * @param eventoId O ID do evento.
     */
    fun addInteresse(participanteId: Long, eventoId: Long) = transaction {
        // 1. Insere a nova relação na tabela de junção
        ParticipantesInteressadosTable.insert {
            it[this.participanteId] = participanteId
            it[this.eventoId] = eventoId
        }

        // 2. Atualiza o contador de likes na tabela de eventos (essencial para consistência)
        EventoTable.update({ EventoTable.id eq eventoId }) {
            with(SqlExpressionBuilder) {
                it.update(numeroLikes, numeroLikes + 1)
            }
        }
    }

    // --- Remover Interesse (Tirar Like) ---
    /**
     * Remove um registro de interesse (like) de um participante em um evento.
     * @param participanteId O ID do participante.
     * @param eventoId O ID do evento.
     */

    fun removeInteresse(participanteId: Long, eventoId: Long): Boolean = transaction {
        // 1. Tenta remover a relação da tabela de junção
        val deletedRows = ParticipantesInteressadosTable.deleteWhere {
            (this.participanteId eq participanteId) and (this.eventoId eq eventoId)
        }

        // 2. Se uma linha foi de fato deletada, decrementa o contador de likes no evento
        if (deletedRows > 0) {
            EventoTable.update({ EventoTable.id eq eventoId }) {
                with(SqlExpressionBuilder) {
                    // Garante que o número de likes não fique negativo
                    it.update(numeroLikes, numeroLikes - 1)
                }
            }
        }

        // 3. Retorna true se a operação de remoção foi bem-sucedida
        return@transaction deletedRows > 0
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