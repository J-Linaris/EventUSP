package br.usp.eventUSP

import br.usp.eventUSP.model.Evento
import br.usp.eventUSP.model.ImagemEvento
import br.usp.eventUSP.model.Review
import br.usp.eventUSP.model.UsuarioOrganizador
import br.usp.eventUSP.model.UsuarioParticipante
import br.usp.eventUSP.repository.EventoRepository
import br.usp.eventUSP.repository.ImagemEventoRepository
import br.usp.eventUSP.repository.ReviewRepository
import br.usp.eventUSP.repository.UsuarioOrganizadorRepository
import br.usp.eventUSP.repository.UsuarioParticipanteRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        route("/api") {
            // Rotas para Eventos
            route("/eventos") {
                val eventoRepository = EventoRepository()

                get {
                    val eventos = eventoRepository.findAll()
                    call.respond(eventos)
                }

                get("/{id}") {
                    val id = call.parameters["id"]?.toLongOrNull()
                        ?: return@get call.respond(HttpStatusCode.BadRequest, "ID inválido")

                    val evento = eventoRepository.findById(id)
                        ?: return@get call.respond(HttpStatusCode.NotFound, "Evento não encontrado")

                    call.respond(evento)
                }

                get("/categoria/{categoria}") {
                    val categoria = call.parameters["categoria"]
                        ?: return@get call.respond(HttpStatusCode.BadRequest, "Categoria não especificada")

                    val eventos = eventoRepository.findByCategoria(categoria)
                    call.respond(eventos)
                }

                get("/futuros") {
                    val eventos = eventoRepository.findFuturos()
                    call.respond(eventos)
                }

                post {
                    val evento = call.receive<Evento>()
                    try {
                        val createdEvento = eventoRepository.create(evento)
                        call.respond(HttpStatusCode.Created, createdEvento)
                    } catch (e: IllegalArgumentException) {
                        call.respond(HttpStatusCode.BadRequest, e.message ?: "Erro ao criar evento")
                    }
                }

                put("/{id}") {
                    val id = call.parameters["id"]?.toLongOrNull()
                        ?: return@put call.respond(HttpStatusCode.BadRequest, "ID inválido")

                    val evento = call.receive<Evento>()
                    evento.id = id

                    try {
                        val updatedEvento = eventoRepository.update(evento)
                        call.respond(updatedEvento)
                    } catch (e: IllegalArgumentException) {
                        call.respond(HttpStatusCode.NotFound, e.message ?: "Evento não encontrado")
                    }
                }

                delete("/{id}") {
                    val id = call.parameters["id"]?.toLongOrNull()
                        ?: return@delete call.respond(HttpStatusCode.BadRequest, "ID inválido")

                    val deleted = eventoRepository.delete(id)
                    if (deleted) {
                        call.respond(HttpStatusCode.NoContent)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Evento não encontrado")
                    }
                }
            }

            // Rotas para Usuários Organizadores
            route("/organizadores") {
                val organizadorRepository = UsuarioOrganizadorRepository()

                get {
                    val organizadores = organizadorRepository.findAll()
                    call.respond(organizadores)
                }

                get("/{id}") {
                    val id = call.parameters["id"]?.toLongOrNull()
                        ?: return@get call.respond(HttpStatusCode.BadRequest, "ID inválido")

                    val organizador = organizadorRepository.findById(id)
                        ?: return@get call.respond(HttpStatusCode.NotFound, "Organizador não encontrado")

                    call.respond(organizador)
                }

                post {
                    val organizador = call.receive<UsuarioOrganizador>()
                    val createdOrganizador = organizadorRepository.create(organizador)
                    call.respond(HttpStatusCode.Created, createdOrganizador)
                }

                put("/{id}") {
                    val id = call.parameters["id"]?.toLongOrNull()
                        ?: return@put call.respond(HttpStatusCode.BadRequest, "ID inválido")

                    val organizador = call.receive<UsuarioOrganizador>()
                    organizador.id = id

                    try {
                        val updatedOrganizador = organizadorRepository.update(organizador)
                        call.respond(updatedOrganizador)
                    } catch (e: IllegalArgumentException) {
                        call.respond(HttpStatusCode.NotFound, e.message ?: "Organizador não encontrado")
                    }
                }

                delete("/{id}") {
                    val id = call.parameters["id"]?.toLongOrNull()
                        ?: return@delete call.respond(HttpStatusCode.BadRequest, "ID inválido")

                    val deleted = organizadorRepository.delete(id)
                    if (deleted) {
                        call.respond(HttpStatusCode.NoContent)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Organizador não encontrado")
                    }
                }
            }

            // Rotas para Usuários Participantes
            route("/participantes") {
                val participanteRepository = UsuarioParticipanteRepository()

                get {
                    val participantes = participanteRepository.findAll()
                    call.respond(participantes)
                }

                get("/{id}") {
                    val id = call.parameters["id"]?.toLongOrNull()
                        ?: return@get call.respond(HttpStatusCode.BadRequest, "ID inválido")

                    val participante = participanteRepository.findById(id)
                        ?: return@get call.respond(HttpStatusCode.NotFound, "Participante não encontrado")

                    call.respond(participante)
                }

                post {
                    val participante = call.receive<UsuarioParticipante>()
                    val createdParticipante = participanteRepository.create(participante)
                    call.respond(HttpStatusCode.Created, createdParticipante)
                }

                put("/{id}") {
                    val id = call.parameters["id"]?.toLongOrNull()
                        ?: return@put call.respond(HttpStatusCode.BadRequest, "ID inválido")

                    val participante = call.receive<UsuarioParticipante>()
                    participante.id = id

                    try {
                        val updatedParticipante = participanteRepository.update(participante)
                        call.respond(updatedParticipante)
                    } catch (e: IllegalArgumentException) {
                        call.respond(HttpStatusCode.NotFound, e.message ?: "Participante não encontrado")
                    }
                }

                delete("/{id}") {
                    val id = call.parameters["id"]?.toLongOrNull()
                        ?: return@delete call.respond(HttpStatusCode.BadRequest, "ID inválido")

                    val deleted = participanteRepository.delete(id)
                    if (deleted) {
                        call.respond(HttpStatusCode.NoContent)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Participante não encontrado")
                    }
                }
            }

            // Rotas para Reviews
            route("/reviews") {
                val reviewRepository = ReviewRepository()

                get {
                    val reviews = reviewRepository.findAll()
                    call.respond(reviews)
                }

                get("/{id}") {
                    val id = call.parameters["id"]?.toLongOrNull()
                        ?: return@get call.respond(HttpStatusCode.BadRequest, "ID inválido")

                    val review = reviewRepository.findById(id)
                        ?: return@get call.respond(HttpStatusCode.NotFound, "Review não encontrada")

                    call.respond(review)
                }

                get("/evento/{eventoId}") {
                    val eventoId = call.parameters["eventoId"]?.toLongOrNull()
                        ?: return@get call.respond(HttpStatusCode.BadRequest, "ID de evento inválido")

                    val reviews = reviewRepository.findByEvento(eventoId)
                    call.respond(reviews)
                }

                get("/participante/{participanteId}") {
                    val participanteId = call.parameters["participanteId"]?.toLongOrNull()
                        ?: return@get call.respond(HttpStatusCode.BadRequest, "ID de participante inválido")

                    val reviews = reviewRepository.findByParticipante(participanteId)
                    call.respond(reviews)
                }

                post {
                    val review = call.receive<Review>()
                    try {
                        val createdReview = reviewRepository.create(review)
                        call.respond(HttpStatusCode.Created, createdReview)
                    } catch (e: IllegalArgumentException) {
                        call.respond(HttpStatusCode.BadRequest, e.message ?: "Erro ao criar review")
                    }
                }

                put("/{id}") {
                    val id = call.parameters["id"]?.toLongOrNull()
                        ?: return@put call.respond(HttpStatusCode.BadRequest, "ID inválido")

                    val review = call.receive<Review>()
                    review.id = id

                    try {
                        val updatedReview = reviewRepository.update(review)
                        call.respond(updatedReview)
                    } catch (e: IllegalArgumentException) {
                        call.respond(HttpStatusCode.NotFound, e.message ?: "Review não encontrada")
                    }
                }

                delete("/{id}") {
                    val id = call.parameters["id"]?.toLongOrNull()
                        ?: return@delete call.respond(HttpStatusCode.BadRequest, "ID inválido")

                    val deleted = reviewRepository.delete(id)
                    if (deleted) {
                        call.respond(HttpStatusCode.NoContent)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Review não encontrada")
                    }
                }
            }

            // Rotas para Imagens de Eventos
            route("/imagens") {
                val imagemRepository = ImagemEventoRepository()

                get {
                    val imagens = imagemRepository.findAll()
                    call.respond(imagens)
                }

                get("/{id}") {
                    val id = call.parameters["id"]?.toLongOrNull()
                        ?: return@get call.respond(HttpStatusCode.BadRequest, "ID inválido")

                    val imagem = imagemRepository.findById(id)
                        ?: return@get call.respond(HttpStatusCode.NotFound, "Imagem não encontrada")

                    call.respond(imagem)
                }

                get("/evento/{eventoId}") {
                    val eventoId = call.parameters["eventoId"]?.toLongOrNull()
                        ?: return@get call.respond(HttpStatusCode.BadRequest, "ID de evento inválido")

                    val imagens = imagemRepository.findByEvento(eventoId)
                    call.respond(imagens)
                }

                get("/evento/{eventoId}/ordenadas") {
                    val eventoId = call.parameters["eventoId"]?.toLongOrNull()
                        ?: return@get call.respond(HttpStatusCode.BadRequest, "ID de evento inválido")

                    val imagens = imagemRepository.findByEventoOrdenadas(eventoId)
                    call.respond(imagens)
                }

                post {
                    val imagem = call.receive<ImagemEvento>()
                    try {
                        val createdImagem = imagemRepository.create(imagem)
                        call.respond(HttpStatusCode.Created, createdImagem)
                    } catch (e: IllegalArgumentException) {
                        call.respond(HttpStatusCode.BadRequest, e.message ?: "Erro ao criar imagem")
                    }
                }

                put("/{id}") {
                    val id = call.parameters["id"]?.toLongOrNull()
                        ?: return@put call.respond(HttpStatusCode.BadRequest, "ID inválido")

                    val imagem = call.receive<ImagemEvento>()
                    imagem.id = id

                    try {
                        val updatedImagem = imagemRepository.update(imagem)
                        call.respond(updatedImagem)
                    } catch (e: IllegalArgumentException) {
                        call.respond(HttpStatusCode.NotFound, e.message ?: "Imagem não encontrada")
                    }
                }

                delete("/{id}") {
                    val id = call.parameters["id"]?.toLongOrNull()
                        ?: return@delete call.respond(HttpStatusCode.BadRequest, "ID inválido")

                    val deleted = imagemRepository.delete(id)
                    if (deleted) {
                        call.respond(HttpStatusCode.NoContent)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Imagem não encontrada")
                    }
                }
            }
        }
    }
}
