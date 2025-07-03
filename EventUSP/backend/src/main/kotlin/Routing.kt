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

fun Application.configureRouting() {
    routing {
        route("/api") {
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

                post {
                    val organizadorRepository = UsuarioOrganizadorRepository()
                    val eventoReq = call.receive<EventoRequest>()

                    // Busca o organizador pelo ID recebido
                    val organizador = organizadorRepository.findById(eventoReq.organizadorId)
                        ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Organizador não encontrado no repositório"))

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

                route("/{id}"){
                    get {
                        val id = call.parameters["id"]?.toLongOrNull()
                            ?: return@get call.respondText("ID inválido", status = HttpStatusCode.BadRequest)

                        val evento = eventoRepository.findById(id)
                            ?: return@get call.respondText("Evento não encontrado", status = HttpStatusCode.NotFound)

                        call.respond(evento)
                    }

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

                    // Por enquanto a autenticação vai ficar somente aqui, mas o correto mesmo seria envolver toda a rota de eventos.
                    authenticate {
                        route("/interesse") {
                            val participanteRepository = UsuarioParticipanteRepository()

                        post {
                            val eventoId = call.parameters["id"]?.toLongOrNull()
                                ?: return@post call.respondText("ID de evento inválido", status = HttpStatusCode.BadRequest)

                            val evento = eventoRepository.findById(eventoId)
                                ?: return@post call.respondText("Evento não encontrado", status = HttpStatusCode.NotFound)

                                // Extrair o ID do usuário do seu token de autenticação (JWTPrincipal, etc.)
                                val participanteId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
                                    ?: return@post call.respondText("ID de usuário inválido", status = HttpStatusCode.BadRequest)

                                val participante = participanteRepository.findById(participanteId)
                                    ?: return@post call.respondText(
                                        "Usuário não encontrado",
                                        status = HttpStatusCode.NotFound
                                    )

                                participante.demonstrarInteresse(evento)

                                val updatedParticipante = participanteRepository.addInteresse(participanteId, eventoId)
                                call.respond(HttpStatusCode.Created, mapOf("message" to "Interesse registrado"))
                            }
                        }
                    }

                    route("/imagens"){
                        val imagemRepository = ImagemEventoRepository()

                        // GET /eventos/{id}/imagens -> Lista imagens de um evento
                        get{
                            val eventoId = call.parameters["id"]?.toLongOrNull()
                                ?: return@get call.respond(HttpStatusCode.BadRequest, "ID de evento inválido")
                            val imagens = imagemRepository.findByEvento(eventoId)
                            call.respond(imagens)
                        }

                        // POST /eventos/{id}/imagens -> Adiciona uma nova imagem a um evento
                        post {
                            // MUDANÇA: Adicionar imagem é um POST no sub-recurso /imagens.
                            val eventoId = call.parameters["id"]?.toLongOrNull()
                                ?: return@post call.respond(HttpStatusCode.BadRequest, "ID de evento inválido")
                            val imagemReq = call.receive<ImagemRequest>()

                            val evento = eventoRepository.findById(eventoId)
                                ?: return@post call.respond(HttpStatusCode.NotFound, "Evento não encontrado")

                            // O ID do evento da imagem é o da URL, não do corpo da requisição
                            val novaImagem = evento.adicionarImagem(
                                url = imagemReq.url,
                                descricao = imagemReq.descricao,
                            )

                            val imagemSalva = imagemRepository.create(novaImagem)
                            call.respond(HttpStatusCode.Created, imagemSalva)
                        }
                    }

                    route("/reviews") {
                        val reviewRepository = ReviewRepository()

                        get {
                            val reviews = reviewRepository.findAll()
                            call.respond(reviews)
                        }

                        get("/{id}") {
                            val id = call.parameters["id"]?.toLongOrNull()
                                ?: return@get call.respondText("ID inválido", status = HttpStatusCode.BadRequest)

                            val review = reviewRepository.findById(id)
                                ?: return@get call.respondText("Review não encontrada", status = HttpStatusCode.NotFound)

                            call.respond(review)
                        }

                        get("/participante/{participanteId}") {
                            val participanteId = call.parameters["participanteId"]?.toLongOrNull()
                                ?: return@get call.respondText("ID de participante inválido", status = HttpStatusCode.BadRequest)

                            val reviews = reviewRepository.findByParticipante(participanteId)
                            call.respond(reviews)
                        }

                        post {
                            val review = call.receive<Review>()
                            try {
                                val createdReview = reviewRepository.create(review)
                                call.respond(HttpStatusCode.Created, createdReview)
                            } catch (e: IllegalArgumentException) {
                                call.respondText(e.message ?: "Erro ao criar review", status = HttpStatusCode.BadRequest)
                            }
                        }

                        put("/{id}") {
                            val id = call.parameters["id"]?.toLongOrNull()
                                ?: return@put call.respondText("ID inválido", status = HttpStatusCode.BadRequest)

                            val review = call.receive<Review>()
                            review.id = id

                            try {
                                val updatedReview = reviewRepository.update(review)
                                call.respond(updatedReview)
                            } catch (e: IllegalArgumentException) {
                                call.respondText(e.message ?: "Review não encontrada", status = HttpStatusCode.NotFound)
                            }
                        }

                        delete("/{id}") {
                            val id = call.parameters["id"]?.toLongOrNull()
                                ?: return@delete call.respondText("ID inválido", status = HttpStatusCode.BadRequest)

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

                put("/{id}") {
                    val id = call.parameters["id"]?.toLongOrNull()
                        ?: return@put call.respondText("ID inválido", status = HttpStatusCode.BadRequest)

                    val organizador = call.receive<UsuarioOrganizador>()
                    organizador.id = id

                    try {
                        val updatedOrganizador = organizadorRepository.update(organizador)
                        call.respond(updatedOrganizador)
                    } catch (e: IllegalArgumentException) {
                        call.respondText(e.message ?: "Organizador não encontrado", status = HttpStatusCode.NotFound)
                    }
                }

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

                put("/{id}") {
                    val id = call.parameters["id"]?.toLongOrNull()
                        ?: return@put call.respondText("ID inválido", status = HttpStatusCode.BadRequest)

                    val participante = call.receive<UsuarioParticipante>()
                    participante.id = id

                    try {
                        val updatedParticipante = participanteRepository.update(participante)
                        call.respond(updatedParticipante)
                    } catch (e: IllegalArgumentException) {
                        call.respondText(e.message ?: "Participante não encontrado", status = HttpStatusCode.NotFound)
                    }
                }

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
            
            // Rotas para Autenticação e Registro de Usuários
            route("/users") {
                val organizadorRepository = UsuarioOrganizadorRepository()
                val participanteRepository = UsuarioParticipanteRepository()
            
                // Rota para registro de usuários
                post("/register") {
                    // Receber os dados do formulário multipart
                    val multipart = call.receiveMultipart()
                    
                    var email = ""
                    var username = ""
                    var password = ""
                    var accountType = ""
                    var profilePhotoPath: String? = null
                    
                    // Processar cada parte do formulário
                    multipart.forEachPart { part ->
                        when (part) {
                            is PartData.FormItem -> {
                                // Processar campos de texto
                                when (part.name) {
                                    "email" -> email = part.value
                                    "username" -> username = part.value
                                    "password" -> password = part.value
                                    "accountType" -> accountType = part.value
                                    // Path lidado aqui
                                    "profilePhoto" -> profilePhotoPath = part.value
                                }
                            }

                            // O código abaixo lidava com a imagem adicionando ela a pasta de uploads
                            // Por enquanto vamos usar imagens apenas com urls públicos da internet, sem precisar armazenar aqui

//                            is PartData.FileItem -> {
//                                // Processar arquivo (foto de perfil)
//                                if (part.name == "profilePhoto") {
//                                    // Criar diretório de uploads se não existir
//                                    val uploadsDir = File("uploads")
//                                    if (!uploadsDir.exists()) {
//                                        uploadsDir.mkdirs()
//                                    }
//
//                                    // Gerar nome único para o arquivo
//                                    val fileName = "${UUID.randomUUID()}_${part.originalFileName}"
//                                    val filePath = File(uploadsDir, fileName)
//
//                                    // Salvar arquivo
//                                    part.streamProvider().use { input ->
//                                        filePath.outputStream().buffered().use { output ->
//                                            input.copyTo(output)
//                                        }
//                                    }
//
//                                    profilePhotoPath = "uploads/$fileName"
//                                }
//                            }
                            else -> {}
                        }
                        part.dispose()
                    }
                    
                    // Verificar se todos os campos obrigatórios foram fornecidos
                    if (email.isBlank() || username.isBlank() || password.isBlank() || accountType.isBlank()) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Todos os campos são obrigatórios"))
                        return@post
                    }
                    
                    try {
                        // Criar o usuário com base no tipo de conta
                        when (accountType) {
                            "organizador" -> {
                                // Verificar se o email já está em uso por outro organizador
                                if (organizadorRepository.findByEmail(email) != null) {
                                    call.respond(HttpStatusCode.Conflict, mapOf("message" to "Email ${email} já está em uso"))
                                    return@post
                                }
                                
                                // Verificar se a foto foi enviada (obrigatória para organizador)
                                if (profilePhotoPath == null) {
                                    call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Foto de perfil é obrigatória para organizadores"))
                                    return@post
                                }

                                // Cria organizador
                                val organizador = UsuarioOrganizador()
                                organizador.id = null
                                organizador.nome = username
                                organizador.email = email
                                organizador.senha = password
                                organizador.fotoPerfil = profilePhotoPath
                                
                                val createdOrganizador = organizadorRepository.create(organizador)
                                
                                // Gerar token JWT
                                val token = generateToken(createdOrganizador.id!!, "organizador")
                                
                                call.respond(HttpStatusCode.Created, UserResponse<UsuarioOrganizador>(
                                    message = "Organizador criado com sucesso",
                                    user = createdOrganizador,
                                    token = token
                                ))
                            }
                            "participante" -> {
                                // Verificar se o email já está em uso por outro participante
                                if (participanteRepository.findByEmail(email) != null) {
                                    call.respond(HttpStatusCode.Conflict, mapOf("message" to "Email ${email} já está em uso"))
                                    return@post
                                }
                                
                                // Criar participante
                                val participante = UsuarioParticipante(
                                    id = null,
                                    nome = username,
                                    email = email,
                                    senha = password // Na implementação real, a senha deve ser hasheada
                                )
                                
                                val createdParticipante = participanteRepository.create(participante)

                                // Gerar token JWT
                                val token = generateToken(createdParticipante.id!!, "participante")
                                
                                call.respond(HttpStatusCode.Created,
                                    UserResponse<UsuarioParticipante>(
                                    message = "Participante criado com sucesso",
                                    user = createdParticipante,
                                    token = token
                                ))
                            }
                            else -> {
                                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Tipo de conta inválido"))
                            }
                        }
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.InternalServerError, mapOf("message" to "Erro ao criar usuário: ${e.message}"))
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
                    }
                    catch (e: Exception) {
                        println("Error processing login request: ${e.message}") // Log de qualquer exceção
                        call.respond(HttpStatusCode.BadRequest, "Erro na requisição: ${e.message}")
                    }

                    call.respond(HttpStatusCode.Unauthorized, "Credenciais inválidas")
                }
            }
        }
    }
}