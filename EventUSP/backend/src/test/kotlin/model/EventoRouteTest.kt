package model

import br.usp.eventUSP.configureRouting
import br.usp.eventUSP.database.tables.*
import br.usp.eventUSP.model.Evento
import br.usp.eventUSP.repository.EventoRepository
import br.usp.eventUSP.repository.UsuarioOrganizadorRepository
import br.usp.eventUSP.EventoRequest
import br.usp.eventUSP.ImagemRequest
import br.usp.eventUSP.LoginRequest
import br.usp.eventUSP.LoginResponse
import br.usp.eventUSP.UserResponse
import br.usp.eventUSP.model.UsuarioOrganizador
import br.usp.eventUSP.model.UsuarioParticipante
import br.usp.eventUSP.repository.ImagemEventoRepository
import io.ktor.server.testing.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SchemaUtils
import org.junit.jupiter.api.*
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import java.time.LocalDateTime
import kotlin.io.readBytes

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventoRouteTest {

    // Lista de todas as tabelas na ordem correta para criação/exclusão.
    private val allTables = arrayOf(
        UsuarioOrganizadorTable,
        UsuarioParticipanteTable,
        EventoTable,
        ImagemEventoTable,
        ReviewTable,
        ParticipantesInteressadosTable
    )

    @BeforeAll
    fun setupDatabase() {
        // Conecta ao banco de dados em memória uma única vez.
        Database.connect(
            "jdbc:h2:mem:test-event;DB_CLOSE_DELAY=-1;",
            driver = "org.h2.Driver",
            user = "root",
            password = ""
        )

        // Cria o schema uma única vez.
        transaction {
            SchemaUtils.create(*allTables)
        }
    }

    // LIMPEZA ANTES DE CADA TESTE: Roda antes de cada @Test.
    @BeforeEach
    fun cleanTables() {
        transaction {
            // A ordem de exclusão é a inversa da criação para respeitar as chaves estrangeiras.
            allTables.reversedArray().forEach { table ->
                table.deleteAll()
            }
        }
    }

    // DESMONTAGEM ÚNICA: Roda apenas uma vez depois de todos os testes da classe.
    @AfterAll
    fun tearDownDatabase() {
        // Boa prática para limpar os recursos, embora o H2 em memória seja descartado de qualquer maneira.
        transaction {
            SchemaUtils.drop(*allTables.reversedArray())
        }
    }

    @Test
    @Timeout(10)
    fun `deve criar um evento e retornar corretamente`() {

        val json = Json { ignoreUnknownKeys = true }

        testApplication {
            application { configureRouting() }

            // Cria organizador usando multipart/form-data
            val createResponse = client.submitFormWithBinaryData(
                url = "/api/users/register",
                formData = formData {
                    append("email", "org3@usp.br")
                    append("username", "Organizador Que Cria Evento")
                    append("password", "orgpass")
                    append("accountType", "organizador")
                    append("profilePhoto","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSABqV-VQBSF1Qyj1Qyo6RiLfpA0McI1leTvQ&s")
                }
            )

            assertEquals(HttpStatusCode.Created, createResponse.status)

            val organizerResponse = json.decodeFromString<UserResponse<UsuarioOrganizador>>(createResponse.bodyAsText())
            assertEquals("Organizador criado com sucesso", organizerResponse.message)
            assertEquals("Organizador Que Cria Evento", organizerResponse.user.nome)
            assertEquals("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSABqV-VQBSF1Qyj1Qyo6RiLfpA0McI1leTvQ&s", organizerResponse.user.fotoPerfil)
            assertNotNull(organizerResponse.token)

            val organizadorId = organizerResponse.user.id!!

            // Faz login com o organizador para criar o evento
            val loginRequest1 = LoginRequest("org3@usp.br", "orgpass")
            val loginResponse1 = client.post("/api/users/login") {
                contentType(ContentType.Application.Json)
                setBody(json.encodeToString(loginRequest1))
            }
            assertEquals(HttpStatusCode.OK, loginResponse1.status, "O login do organizador falhou")

            val loginData1 = Json.decodeFromString<LoginResponse<UsuarioOrganizador>>(loginResponse1.bodyAsText())
            val tokenOrganizador = loginData1.token
            assertNotNull(tokenOrganizador)

            val eventoRequest = EventoRequest(
                titulo = "Festa de Integração",
                descricao = "Evento para integração dos alunos",
                dataHora = LocalDateTime.now().plusDays(3).toString(), // Formato ISO-8601
                localizacao = "USP Butantã",
                categoria = "Festa",
            )

            // Envia requisição para criar evento
            val response = client.post("/api/eventos") {
                contentType(ContentType.Application.Json)
                setBody(json.encodeToString(eventoRequest))
                header(HttpHeaders.Authorization, "Bearer $tokenOrganizador")
            }

            println(response.status)

            assertEquals(HttpStatusCode.Companion.Created, response.status)

            // Valida se evento foi criado na resposta
            val createdEvento = json.decodeFromString<Evento>(response.bodyAsText())
            assertEquals(eventoRequest.titulo, createdEvento.titulo)
            assertEquals(eventoRequest.descricao, createdEvento.descricao)
            assertEquals(eventoRequest.localizacao, createdEvento.localizacao)
            assertEquals(organizadorId, createdEvento.organizador.id)
            // assertEquals(1, createdEvento.imagens.size)
            // assertEquals("https://link.com/img.jpg", createdEvento.imagens[0].url)

            // Valida se evento foi salvo no banco (repositório)
            val eventRepo = EventoRepository()
            val salvo = eventRepo.findById(createdEvento.id!!)
            assertNotNull(salvo)
            assertEquals(eventoRequest.titulo, salvo.titulo)
            assertEquals(organizadorId, salvo.organizador.id)
            //   assertTrue(salvo.imagens.any { it.url == "https://link.com/img.jpg" } ?: false)
        }
    }

    @Test
    @Timeout(10)
    fun `deve criar um evento e adicionar imagens corretamente`() {

        val json = Json { ignoreUnknownKeys = true }

        testApplication {
            application { configureRouting() }

            // Cria organizador usando multipart/form-data
            val createResponse = client.submitFormWithBinaryData(
                url = "/api/users/register",
                formData = formData {
                    append("email", "org4@usp.br")
                    append("username", "Organizador que cria e bota imagem")
                    append("password", "orgpass")
                    append("accountType", "organizador")
                    append("profilePhoto","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSABqV-VQBSF1Qyj1Qyo6RiLfpA0McI1leTvQ&s")
                }
            )

            assertEquals(HttpStatusCode.Created, createResponse.status)

            val organizerResponse = json.decodeFromString<UserResponse<UsuarioOrganizador>>(createResponse.bodyAsText())
            assertEquals("Organizador criado com sucesso", organizerResponse.message)
            assertEquals("Organizador que cria e bota imagem", organizerResponse.user.nome)
            assertEquals("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSABqV-VQBSF1Qyj1Qyo6RiLfpA0McI1leTvQ&s", organizerResponse.user.fotoPerfil)
            assertNotNull(organizerResponse.token)

            val organizadorId = organizerResponse.user.id!!

            // Faz login com o organizador para criar o evento
            val loginRequest1 = LoginRequest("org4@usp.br", "orgpass")
            val loginResponse1 = client.post("/api/users/login") {
                contentType(ContentType.Application.Json)
                setBody(json.encodeToString(loginRequest1))
            }
            assertEquals(HttpStatusCode.OK, loginResponse1.status, "O login do organizador falhou")

            val loginData1 = Json.decodeFromString<LoginResponse<UsuarioOrganizador>>(loginResponse1.bodyAsText())
            val tokenOrganizador = loginData1.token
            assertNotNull(tokenOrganizador)

            val eventoRequest = EventoRequest(
                titulo = "JunIME",
                descricao = "Festa junina do IME",
                dataHora = LocalDateTime.now().plusDays(7).toString(), // Formato ISO-8601
                localizacao = "Estacionamento do bloco B do IME",
                categoria = "Festa",
            )

            // Envia requisição para criar evento
            val eventoResponse = client.post("/api/eventos") {
                contentType(ContentType.Application.Json)
                setBody(json.encodeToString(eventoRequest))
                header(HttpHeaders.Authorization, "Bearer $tokenOrganizador")
            }

            assertEquals(HttpStatusCode.Companion.Created, eventoResponse.status)

            // Valida se evento foi criado na resposta
            val createdEvento = json.decodeFromString<Evento>(eventoResponse.bodyAsText())
            assertEquals(eventoRequest.titulo, createdEvento.titulo)
            assertEquals(eventoRequest.descricao, createdEvento.descricao)
            assertEquals(eventoRequest.localizacao, createdEvento.localizacao)
            assertEquals(organizadorId, createdEvento.organizador.id)

            // Valida se evento foi salvo no banco (repositório)
            val eventRepo = EventoRepository()
            val eventoSalvo = eventRepo.findById(createdEvento.id!!)
            assertNotNull(eventoSalvo)
            assertEquals(eventoRequest.titulo, eventoSalvo.titulo)
            assertEquals(organizadorId, eventoSalvo.organizador.id)

            val eventoId = eventoSalvo.id!!

            // Cria a imagem usando os dados do evento já salvo
            val imagemRequest = ImagemRequest(
                url = "https://link.com/img.jpg",
                descricao = "Imagem do evento JunIME",
            )

            val imagemResponse = client.post("/api/eventos/$eventoId/imagens") {
                contentType(ContentType.Application.Json)
                setBody(json.encodeToString(imagemRequest))
                header(HttpHeaders.Authorization, "Bearer $tokenOrganizador")
            }

            println(imagemResponse.status)

            assertEquals(HttpStatusCode.Companion.Created, imagemResponse.status)

            val imagemRepo = ImagemEventoRepository()
//            val imagemSalva = imagemRepo.findById(imagemRequest.eventoId)
            val imagemSalva = eventRepo.findById(eventoSalvo.id!!)?.imagens?.get(0)

            // Asserts para ver se a imagem foi salva corretamente no banco de dados
            assertNotNull(imagemSalva)
            assertEquals(imagemRequest.url, imagemSalva.url)
            assertEquals(imagemRequest.descricao, imagemSalva.descricao)
//            assertEquals(imagemRequest.eventoId, imagemSalva.eventoId)

            // Asserts para ver se a imagem foi salva corretamente no evento já criado antes
            val eventoAtualizado = eventRepo.findById(createdEvento.id!!)
            assertNotNull(eventoAtualizado)
            assertEquals("https://link.com/img.jpg", eventoAtualizado.imagens[0].url)
            assertEquals(1, eventoAtualizado.imagens.size)

        }
    }
}