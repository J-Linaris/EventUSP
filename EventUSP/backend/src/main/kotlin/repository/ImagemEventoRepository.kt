package br.usp.eventUSP.repository

import br.usp.eventUSP.database.dao.EventoDAO
import br.usp.eventUSP.database.dao.ImagemEventoDAO
import br.usp.eventUSP.model.ImagemEvento
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Repositório para operações relacionadas a Imagens de Eventos
 */
class ImagemEventoRepository {
    /**
     * Cria uma nova imagem de evento no banco de dados
     * @param imagem A imagem de evento a ser criada
     * @return A imagem de evento criada com o ID gerado
     */
    fun create(imagem: ImagemEvento): ImagemEvento = transaction {
    //    val eventoDAO = EventoDAO.findById(imagem.eventoId!!)
     //       ?: throw IllegalArgumentException("Evento não encontrado")

        val imagemDAO = ImagemEventoDAO.new {
        //    evento = eventoDAO
            url = imagem.url
            descricao = imagem.descricao
            ordem = imagem.ordem
        }

        imagemDAO.toModel()
    }

    /**
     * Busca uma imagem de evento pelo ID
     * @param id O ID da imagem de evento
     * @return A imagem de evento encontrada ou null se não existir
     */
    fun findById(id: Long): ImagemEvento? = transaction {
        ImagemEventoDAO.findById(id)?.toModel()
    }

//    /**
//     * Busca todas as imagens de um evento
//     * @param eventoId O ID do evento
//     * @return Lista de imagens do evento
//     */
//    fun findByEvento(eventoId: Long): List<ImagemEvento> = transaction {
//        ImagemEventoDAO.find { br.usp.eventUSP.database.tables.ImagemEventoTable.eventoId eq eventoId }
//            .map { it.toModel() }
//    }

//    /**
//     * Busca todas as imagens de um evento ordenadas
//     * @param eventoId O ID do evento
//     * @return Lista de imagens do evento ordenadas pelo campo ordem
//     */
//    fun findByEventoOrdenadas(eventoId: Long): List<ImagemEvento> = transaction {
//        ImagemEventoDAO.find { br.usp.eventUSP.database.tables.ImagemEventoTable.eventoId eq eventoId }
//            .orderBy(br.usp.eventUSP.database.tables.ImagemEventoTable.ordem to SortOrder.ASC)
//            .map { it.toModel() }
//    }

    /**
     * Busca todas as imagens de eventos
     * @return Lista de todas as imagens de eventos
     */
    fun findAll(): List<ImagemEvento> = transaction {
        ImagemEventoDAO.all().map { it.toModel() }
    }

    /**
     * Atualiza uma imagem de evento existente
     * @param imagem A imagem de evento com as informações atualizadas
     * @return A imagem de evento atualizada
     */
    fun update(imagem: ImagemEvento): ImagemEvento = transaction {
        val imagemDAO = ImagemEventoDAO.findById(imagem.id!!)
            ?: throw IllegalArgumentException("Imagem de evento não encontrada")

        imagemDAO.url = imagem.url
        imagemDAO.descricao = imagem.descricao
        imagemDAO.ordem = imagem.ordem

        imagemDAO.toModel()
    }

    /**
     * Remove uma imagem de evento pelo ID
     * @param id O ID da imagem de evento a ser removida
     * @return true se a imagem de evento foi removida, false caso contrário
     */
    fun delete(id: Long): Boolean = transaction {
        val imagemDAO = ImagemEventoDAO.findById(id) ?: return@transaction false
        imagemDAO.delete()
        true
    }
}