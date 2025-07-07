package br.usp.eventUSP

import br.usp.eventUSP.model.Evento
import br.usp.eventUSP.model.ImagemEvento
import br.usp.eventUSP.model.Review
import br.usp.eventUSP.model.UsuarioOrganizador
import br.usp.eventUSP.model. UsuarioParticipante
import br.usp.eventUSP.repository.EventoRepository
import br.usp.eventUSP.repository.ImagemEventoRepository
import br.usp.eventUSP.repository.ReviewRepository
import br.usp.eventUSP.repository.UsuarioOrganizadorRepository
import br.usp.eventUSP.repository.UsuarioParticipanteRepository
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.util.*
import kotlinx.serialization.json.Json
import java.time.LocalDateTime

// Definição de uma classe de dados para a requisição de interesse
@kotlinx.serialization.Serializable
data class InteresseRequest(val eventoId: Long)

fun Application.configureRouting() {
    routing {
        route("/api") {
            // --- NOVA ROTA DE NÍVEL SUPERIOR PARA INTERESSE ---
            route("/interesse") {
                val participanteRepository = UsuarioParticipanteRepository()
                val eventoRepository = EventoRepository()

                authenticate {
                    post {
                        val request = call.receive<InteresseRequest>()
                        val eventoId = request.eventoId

                        val participanteId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                            ?: return@post call.respond(HttpStatusCode.Unauthorized)

                        // Validações
                        if (eventoRepository.findById(eventoId) == null) {
                            return@post call.respond(HttpStatusCode.NotFound, mapOf("message" to "Evento não encontrado"))
                        }
                        if (participanteRepository.findById(participanteId) == null) {
                            return@post call.respond(HttpStatusCode.NotFound, mapOf("message" to "Participante não encontrado"))
                        }

                        // Adiciona o interesse
                        participanteRepository.addInteresse(participanteId, eventoId)
                        call.respond(HttpStatusCode.Created, mapOf("message" to "Interesse registrado"))
                    }

                    delete {
                        val request = call.receive<InteresseRequest>()
                        val eventoId = request.eventoId

                        val participanteId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                            ?: return@delete call.respond(HttpStatusCode.Unauthorized)

                        // Remove o interesse
                        val success = participanteRepository.removeInteresse(participanteId, eventoId)

                        if (success) {
                            call.respond(HttpStatusCode.OK, mapOf("message" to "Interesse removido"))
                        } else {
                            call.respond(HttpStatusCode.NotFound, mapOf("message" to "Interesse não encontrado"))
                        }
                    }
                }
            }

            // Rotas para Eventos

            route("/eventos") {
                val eventoRepository = EventoRepository()

                get {
                    val categoria = call.request.queryParameters["categoria"]
                    val periodo = call.request.queryParameters["periodo"]

                    val eventos = when {
                        categoria != null -> eventoRepository.findByCategoria(categoria)
                        periodo == "futuros" -> eventoRepository.findFuturos()
                        else -> eventoRepository.findAll()
                    }
                    call.respond(eventos)
                }
                authenticate {
                    post {
                        val organizadorRepository = UsuarioOrganizadorRepository()
                        val eventoReq = call.receive<EventoRequest>()

                        val organizadorId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                            ?: return@post call.respond("ID de organizador inválido")
                        // Busca o organizador pelo ID recebido
                        val organizador = organizadorRepository.findById(organizadorId)
                            ?: return@post call.respond(
                                HttpStatusCode.BadRequest,
                                mapOf("error" to "Organizador não encontrado no repositório")
                            )

                        // Criação do evento usando o organizador
                        val evento = organizador.criarEvento(
                            eventoReq.titulo,
                            eventoReq.descricao,
                            LocalDateTime.parse(eventoReq.dataHora),
                            eventoReq.localizacao,
                            eventoReq.categoria
                        )
                        // Persiste no banco
                        val eventoSalvo = eventoRepository.create(evento)

                        call.respond(HttpStatusCode.Created, eventoSalvo)
                    }
                }
                route("/{id}") {
                    get {
                        val id = call.parameters["id"]?.toLongOrNull()
                            ?: return@get call.respondText("ID inválido", status = HttpStatusCode.BadRequest)

                        val evento = eventoRepository.findById(id)
                            ?: return@get call.respondText("Evento não encontrado", status = HttpStatusCode.NotFound)

                        call.respond(evento)
                    }
                    authenticate {
                        put {
                            val id = call.parameters["id"]?.toLongOrNull()
                                ?: return@put call.respondText("ID inválido", status = HttpStatusCode.BadRequest)

                            val evento = call.receive<Evento>().apply { this.id = id }

                            try {
                                val updatedEvento = eventoRepository.update(evento)
                                call.respond(updatedEvento)
                            } catch (e: IllegalArgumentException) {
                                call.respondText(e.message ?: "Evento não encontrado", status = HttpStatusCode.NotFound)
                            }
                        }

                        delete {
                            val id = call.parameters["id"]?.toLongOrNull()
                                ?: return@delete call.respondText("ID inválido", status = HttpStatusCode.BadRequest)

                            if (eventoRepository.delete(id)) {
                                call.respond(HttpStatusCode.NoContent)
                            } else {
                                call.respondText("Evento não encontrado", status = HttpStatusCode.NotFound)
                            }
                        }
                    }

                    route("/interesse") {
                            val participanteRepository = UsuarioParticipanteRepository()

                        authenticate {
                            post {
                                val eventoId = call.parameters["id"]?.toLongOrNull()
                                    ?: return@post call.respondText(
                                        "ID de evento inválido",
                                        status = HttpStatusCode.BadRequest
                                    )

                                val evento = eventoRepository.findById(eventoId)
                                    ?: return@post call.respondText(
                                        "Evento não encontrado",
                                        status = HttpStatusCode.NotFound
                                    )

                                // Extrair o ID do usuário do seu token de autenticação (JWTPrincipal)
                                val participanteId =
                                    call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                                        ?: return@post call.respondText(
                                            "ID de usuário inválido",
                                            status = HttpStatusCode.BadRequest
                                        )

                                val participante = participanteRepository.findById(participanteId)
                                    ?: return@post call.respondText(
                                        "Usuário não encontrado",
                                        status = HttpStatusCode.NotFound
                                    )

                                participante.demonstrarInteresse(evento)

                                val updatedParticipante = participanteRepository.addInteresse(participanteId, eventoId)
                                call.respond(HttpStatusCode.Created, mapOf("message" to "Interesse registrado"))

                            }
                            // NOVA ROTA para remover o interesse
                            delete {
                                val eventoId = call.parameters["id"]?.toLongOrNull()
                                    ?: return@delete call.respondText(
                                        "ID de evento inválido",
                                        status = HttpStatusCode.BadRequest
                                    )

                                // Extrai o ID do usuário do token JWT
                                val participanteId =
                                    call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                                        ?: return@delete call.respondText(
                                            "ID de usuário inválido",
                                            status = HttpStatusCode.BadRequest
                                        )

                                // Chama o novo método do repositório para remover o interesse
                                val success = participanteRepository.removeInteresse(participanteId, eventoId)

                                if (success) {
                                    call.respond(HttpStatusCode.OK, mapOf("message" to "Interesse removido com sucesso"))
                                } else {
                                    call.respond(HttpStatusCode.NotFound, mapOf("message" to "Interesse não encontrado ou já removido"))
                                }
                            }
                        }
                    }
                    route("/likes") {
                        // GET /eventos/{id}/likes -> Retorna o número de likes de um evento
                        get {
                            val eventoId = call.parameters["id"]?.toLongOrNull()
                                ?: return@get call.respond(HttpStatusCode.BadRequest, "ID de evento inválido")

                            val evento = eventoRepository.findById(eventoId)
                                ?: return@get call.respond(HttpStatusCode.NotFound, "Evento não encontrado")

                            // Retorna um objeto JSON para seguir as boas práticas de API
                            call.respond(mapOf("likes" to evento.numeroLikes))
                        }
                    }
                    route("/imagens") {
                        val imagemRepository = ImagemEventoRepository()

                        // GET /eventos/{id}/imagens -> Lista imagens de um evento
                        get {
                            val eventoId = call.parameters["id"]?.toLongOrNull()
                                ?: return@get call.respond(HttpStatusCode.BadRequest, "ID de evento inválido")
                            val imagens = imagemRepository.findByEvento(eventoId)
                            call.respond(imagens)
                        }

                        // POST /eventos/{id}/imagens -> Adiciona uma nova imagem a um evento
                        post {
                            // Adicionar imagem é um POST no sub-recurso /imagens.
                            val eventoId = call.parameters["id"]?.toLongOrNull()
                                ?: return@post call.respond(HttpStatusCode.BadRequest, "ID de evento inválido")
                            val imagemReq = call.receive<ImagemRequest>()

                            val evento = eventoRepository.findById(eventoId)
                                ?: return@post call.respond(HttpStatusCode.NotFound, "Evento não encontrado")


                            // O ID do evento da imagem é o da URL, não do corpo da requisição
//                            val novaImagem = evento.adicionarImagem(
//                                url = imagemReq.url,
//                                descricao = imagemReq.descricao,
//                            )
                            val contagemImagensAtuais = imagemRepository.findByEvento(eventoId).size

                            val novaImagem = ImagemEvento(
                                eventoId = eventoId,
                                url = imagemReq.url,
                                descricao = imagemReq.descricao, // Você pode querer pegar isso do request também
                                ordem = contagemImagensAtuais // Usa a contagem real do BD
                            )

                            val imagemSalva = imagemRepository.create(novaImagem)

                            // 1. Busca a lista completa e atualizada de imagens para o evento no banco
                            val imagensNoBanco = imagemRepository.findByEvento(eventoId) //

                            // 2. Imprime o log no console do servidor
                            println("--- LOG DE IMAGENS ---")
                            println("Sucesso! Imagem adicionada ao Evento ID: $eventoId.")
                            println("Total de imagens para este evento no banco: ${imagensNoBanco.size}")
                            println("Lista de Imagens: $imagensNoBanco")
                            println("----------------------")


                            call.respond(HttpStatusCode.Created, imagemSalva)
                        }
                    }

                    route("/reviews") {
                        val reviewRepository = ReviewRepository()
                        val participanteRepository = UsuarioParticipanteRepository()

                        get {
                            // 1. Pega o ID do evento da URL.
                            val eventoId = call.parameters["id"]?.toLongOrNull()
                                ?: return@get call.respondText("ID de evento inválido", status = HttpStatusCode.BadRequest)

                            // 2. Usa o método findByEvento, que já existe no seu repositório.
                            val reviews = reviewRepository.findByEvento(eventoId)

                            call.respond(reviews)
                        }

                        get("/{id}") {
                            val id = call.parameters["id"]?.toLongOrNull()
                                ?: return@get call.respondText(
                                    "ID inválido",
                                    status = HttpStatusCode.BadRequest
                                )

                            val review = reviewRepository.findById(id)
                                ?: return@get call.respondText(
                                    "Review não encontrada",
                                    status = HttpStatusCode.NotFound
                                )

                            call.respond(review)
                        }

                        get("/participante/{participanteId}") {
                            val participanteId = call.parameters["participanteId"]?.toLongOrNull()
                                ?: return@get call.respondText(
                                    "ID de participante inválido",
                                    status = HttpStatusCode.BadRequest
                                )

                            val reviews = reviewRepository.findByParticipante(participanteId)
                            call.respond(reviews)
                        }
                        authenticate {
                            post {
                                val eventoId = call.parameters["id"]?.toLongOrNull()
                                    ?: return@post call.respondText(
                                        "ID de evento inválido",
                                        status = HttpStatusCode.BadRequest
                                    )

                                val evento = eventoRepository.findById(eventoId)
                                    ?: return@post call.respondText(
                                        "Evento não encontrado",
                                        status = HttpStatusCode.NotFound
                                    )

                                // Extrair o ID do usuário do seu token de autenticação (JWTPrincipal)
                                val participanteId =
                                    call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                                        ?: return@post call.respondText(
                                            "ID de usuário inválido",
                                            status = HttpStatusCode.BadRequest
                                        )

                                val participante = participanteRepository.findById(participanteId)
                                    ?: return@post call.respondText(
                                        "Usuário não encontrado",
                                        status = HttpStatusCode.NotFound
                                    )

                                val reviewReq = call.receive<ReviewRequest>()

                                val novaReview = participante.adicionarReview(
                                    evento = evento,
                                    nota = reviewReq.nota,
                                    comentario = reviewReq.comentario
                                ) ?: return@post call.respondText(

                                    "Erro ao criar review",
                                    status = HttpStatusCode.Forbidden,
                                )

                                val reviewSalva = reviewRepository.create(novaReview)

                                // Em vez de enviar um mapa com uma mensagem, envie o objeto da review salva.
                                call.respond(HttpStatusCode.Created, reviewSalva)

                            }
                        }
                        authenticate {
                            put("/{id}") {
                                val id = call.parameters["id"]?.toLongOrNull()
                                    ?: return@put call.respondText(
                                        "ID inválido",
                                        status = HttpStatusCode.BadRequest
                                    )

                                val review = call.receive<Review>()
                                review.id = id

                                try {
                                    val updatedReview = reviewRepository.update(review)
                                    call.respond(updatedReview)
                                } catch (e: IllegalArgumentException) {
                                    call.respondText(
                                        e.message ?: "Review não encontrada",
                                        status = HttpStatusCode.NotFound
                                    )
                                }
                            }
                        }
                        authenticate {
                            delete("/{id}") {
                                val id = call.parameters["id"]?.toLongOrNull()
                                    ?: return@delete call.respondText(
                                        "ID inválido",
                                        status = HttpStatusCode.BadRequest
                                    )

                                val deleted = reviewRepository.delete(id)
                                if (deleted) {
                                    call.respond(HttpStatusCode.NoContent)
                                } else {
                                    call.respondText("Review não encontrada", status = HttpStatusCode.NotFound)
                                }
                            }
                        }
                    }
                }
            }
            // --- NOVA ROTA DE NÍVEL SUPERIOR PARA REVIEWS ---
            route("/reviews") {
                val reviewRepository = ReviewRepository()

                // Você pode mover outras rotas de review para cá se quiser,
                // mas vamos focar apenas na de exclusão por enquanto.
                authenticate {
                    delete("/{id}") {
                        val reviewId = call.parameters["id"]?.toLongOrNull()
                            ?: return@delete call.respondText(
                                "ID de review inválido",
                                status = HttpStatusCode.BadRequest
                            )

                        // Validação extra (opcional mas recomendada):
                        // Verificar se o usuário que está deletando é o dono da review
                        val participanteId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                        val review = reviewRepository.findById(reviewId)

                        if (review == null) {
                            return@delete call.respondText("Review não encontrada", status = HttpStatusCode.NotFound)
                        }

                        if (review.participanteId != participanteId) {
                            return@delete call.respond(HttpStatusCode.Forbidden, "Você não tem permissão para excluir esta review.")
                        }

                        // Se tudo estiver OK, deleta
                        val deleted = reviewRepository.delete(reviewId)
                        if (deleted) {
                            call.respond(HttpStatusCode.NoContent)
                        } else {
                            // Este caso é coberto pelo findById acima, mas é bom manter por segurança
                            call.respondText("Review não encontrada", status = HttpStatusCode.NotFound)
                        }
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
                        ?: return@get call.respondText("ID inválido", status = HttpStatusCode.BadRequest)

                    val organizador = organizadorRepository.findById(id)
                        ?: return@get call.respondText("Organizador não encontrado", status = HttpStatusCode.NotFound)

                    call.respond(organizador)
                }

                post {
                    val organizador = call.receive<UsuarioOrganizador>()
                    val createdOrganizador = organizadorRepository.create(organizador)
                    call.respond(HttpStatusCode.Created, createdOrganizador)
                }
                authenticate {
                    put("/{id}") {
                        val id = call.parameters["id"]?.toLongOrNull()
                            ?: return@put call.respondText("ID inválido", status = HttpStatusCode.BadRequest)

                        val organizador = call.receive<UsuarioOrganizador>()
                        organizador.id = id

                        try {
                            val updatedOrganizador = organizadorRepository.update(organizador)
                            call.respond(updatedOrganizador)
                        } catch (e: IllegalArgumentException) {
                            call.respondText(
                                e.message ?: "Organizador não encontrado",
                                status = HttpStatusCode.NotFound
                            )
                        }
                    }
                }
                authenticate {
                    delete("/{id}") {
                        val id = call.parameters["id"]?.toLongOrNull()
                            ?: return@delete call.respondText("ID inválido", status = HttpStatusCode.BadRequest)

                        val deleted = organizadorRepository.delete(id)
                        if (deleted) {
                            call.respond(HttpStatusCode.NoContent)
                        } else {
                            call.respondText("Organizador não encontrado", status = HttpStatusCode.NotFound)
                        }
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
                        ?: return@get call.respondText("ID inválido", status = HttpStatusCode.BadRequest)

                    val participante = participanteRepository.findById(id)
                        ?: return@get call.respondText("Participante não encontrado", status = HttpStatusCode.NotFound)

                    call.respond(participante)
                }
                authenticate {
                    put("/{id}") {
                        val id = call.parameters["id"]?.toLongOrNull()
                            ?: return@put call.respondText("ID inválido", status = HttpStatusCode.BadRequest)

                        val participante = call.receive<UsuarioParticipante>()
                        participante.id = id

                        try {
                            val updatedParticipante = participanteRepository.update(participante)
                            call.respond(updatedParticipante)
                        } catch (e: IllegalArgumentException) {
                            call.respondText(
                                e.message ?: "Participante não encontrado",
                                status = HttpStatusCode.NotFound
                            )
                        }
                    }
                }
                authenticate {
                    delete("/{id}") {
                        val id = call.parameters["id"]?.toLongOrNull()
                            ?: return@delete call.respondText("ID inválido", status = HttpStatusCode.BadRequest)

                        val deleted = participanteRepository.delete(id)
                        if (deleted) {
                            call.respond(HttpStatusCode.NoContent)
                        } else {
                            call.respondText("Participante não encontrado", status = HttpStatusCode.NotFound)
                        }
                    }
                }
            }

            // Rotas para Autenticação e Registro de Usuários
            route("/users") {
                val organizadorRepository = UsuarioOrganizadorRepository()
                val participanteRepository = UsuarioParticipanteRepository()

                // Rota para registro de usuários
                post("/register") {
                    try {
                        // 1. Recebe o corpo da requisição e desserializa para o objeto RegisterRequest
                        val request = call.receive<RegisterRequest>()

                        // 2. Validações básicas
                        if (request.email.isBlank() || request.username.isBlank() || request.password.isBlank() || request.accountType.isBlank()) {
                            call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Todos os campos são obrigatórios"))
                            return@post
                        }

                        // 3. Lógica de criação de usuário baseada no tipo de conta
                        when (request.accountType) {
                            "organizador" -> {
                                // Verificar se o email já está em uso
                                if (organizadorRepository.findByEmail(request.email) != null) {
                                    return@post call.respond(
                                        HttpStatusCode.Conflict,
                                        mapOf("message" to "Email ${request.email} já está em uso")
                                    )
                                }

                                // Verificar se a URL da foto foi enviada (obrigatória para organizador)
                                if (request.profilePhoto.isNullOrBlank()) {
                                    return@post call.respond(
                                        HttpStatusCode.BadRequest,
                                        mapOf("message" to "URL da foto de perfil é obrigatória para organizadores")
                                    )
                                }

                                // Cria o objeto organizador com os dados da requisição
                                val organizador = UsuarioOrganizador().apply {
                                    this.id = null
                                    this.nome = request.username
                                    this.email = request.email
                                    this.senha = request.password // Lembre-se de usar hash em produção!
                                    this.fotoPerfil = request.profilePhoto
                                }

                                val createdOrganizador = organizadorRepository.create(organizador)
                                val token = generateToken(createdOrganizador.id!!, "organizador")

                                call.respond(
                                    HttpStatusCode.Created, UserResponse(
                                        message = "Organizador criado com sucesso",
                                        user = createdOrganizador,
                                        token = token
                                    )
                                )
                            }

                            "participante" -> {
                                // Verificar se o email já está em uso
                                if (participanteRepository.findByEmail(request.email) != null) {
                                    return@post call.respond(
                                        HttpStatusCode.Conflict,
                                        mapOf("message" to "Email ${request.email} já está em uso")
                                    )
                                }

                                // Cria o objeto participante
                                val participante = UsuarioParticipante(
                                    id = null,
                                    nome = request.username,
                                    email = request.email,
                                    senha = request.password
                                )

                                val createdParticipante = participanteRepository.create(participante)
                                val token = generateToken(createdParticipante.id!!, "participante")

                                call.respond(
                                    HttpStatusCode.Created,
                                    UserResponse(
                                        message = "Participante criado com sucesso",
                                        user = createdParticipante,
                                        token = token
                                    )
                                )
                            }

                            else -> {
                                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Tipo de conta inválido"))
                            }
                        }
                    } catch (e: Exception) {
                        // Captura erros de desserialização ou outros problemas
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            mapOf("message" to "Erro ao processar a requisição: ${e.message}")
                        )
                    }
                }

                // Rota de login
                post("/login") {
                    try {
                        val requestBody = call.receiveText() // Recebe como texto primeiro para inspecionar
                        println("Received login request body: $requestBody") // Log do corpo bruto
                        val loginRequest =
                            Json.decodeFromString<LoginRequest>(requestBody) // Tenta desserializar manualmente para pegar o erro

//                        val body = call.receive<LoginRequest>()

//                        val organizador = organizadorRepo.findByEmail(body.email)
                        val organizador = organizadorRepository.findByEmail(loginRequest.email)
                        if (organizador != null && organizador.senha == loginRequest.password) {
//                        if (organizador != null && organizador.senha == body.senha) {

                            // Gera Token do Organizador logado
                            val token = generateToken(organizador.id!!, "organizador")

                            call.respond(
                                LoginResponse<UsuarioOrganizador>(
                                    message = "Login efetuado com sucesso!",
                                    token = token,
                                    user = organizador,
                                    role = "organizador"
                                )
                            )
                            return@post
                        }

//                      val participante = participanteRepo.findByEmail(body.email)
                        val participante = participanteRepository.findByEmail(loginRequest.email)
//                        if (participante != null && participante.senha == body.senha) {
                        if (participante != null && participante.senha == loginRequest.password) {

                            // Gera Token do Participante logado
                            val token = generateToken(participante.id!!, "participante")

                            call.respond(
                                LoginResponse<UsuarioParticipante>(
                                    message = "Login efetuado com sucesso!",
                                    token = token,
                                    user = participante,
                                    role = "participante"
                                )
                            )
                            return@post
                        }
                    } catch (e: Exception) {
                        println("Error processing login request: ${e.message}") // Log de qualquer exceção
                        call.respond(HttpStatusCode.BadRequest, "Erro na requisição: ${e.message}")
                    }

                    call.respond(HttpStatusCode.Unauthorized, "Credenciais inválidas")
                }

            }
        }
    }
}