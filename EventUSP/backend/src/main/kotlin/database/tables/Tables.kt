package br.usp.eventUSP.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.Table

/**
 * Tabela para armazenar os eventos
 */
object EventoTable : LongIdTable("eventos") {
    val titulo = varchar("titulo", 255)
    val descricao = text("descricao")
    val dataHora = datetime("data_hora")
    val localizacao = varchar("localizacao", 255)
    val imagens = varchar("imagens", 255)
    val categoria = varchar("categoria", 100)
    val organizadorId = reference("organizador_id", UsuarioOrganizadorTable)
    val numeroLikes = integer("numero_likes").default(0)
}

/**
 * Tabela para armazenar os usuários organizadores
 */
object UsuarioOrganizadorTable : LongIdTable("usuarios_organizadores") {
    val nome = varchar("nome", 255)
    val email = varchar("email", 255).uniqueIndex()
    val senha = varchar("senha", 255)
    val fotoPerfil = varchar("foto_perfil",255).nullable()
    // Adicione outros campos conforme necessário
}

/**
 * Tabela para armazenar os usuários participantes
 */
object UsuarioParticipanteTable : LongIdTable("usuarios_participantes") {
    val nome = varchar("nome", 255)
    val email = varchar("email", 255).uniqueIndex()
    val senha = varchar("senha", 255)
    // Adicione outros campos conforme necessário
}

/**
 * Tabela para armazenar as reviews dos eventos
 */
object ReviewTable : LongIdTable("reviews") {
    val eventoId = reference("evento_id", EventoTable)
    val participanteId = reference("participante_id", UsuarioParticipanteTable)
    val nota = integer("nota")
    val comentario = text("comentario")
}

/**
 * Tabela para armazenar as imagens dos eventos
 */
object ImagemEventoTable : LongIdTable("imagens_evento") {
    val eventoId = reference("evento_id", EventoTable)
    val url = varchar("url", 255)
    val descricao = varchar("descricao", 255).nullable()
    val ordem = integer("ordem")
}

/**
 * Tabela de relacionamento muitos-para-muitos entre Eventos e Participantes interessados
 */
object ParticipantesInteressadosTable : Table("participantes_interessados") {
    val eventoId = reference("evento_id", EventoTable)
    val participanteId = reference("participante_id", UsuarioParticipanteTable)
    
    override val primaryKey = PrimaryKey(eventoId, participanteId)
}