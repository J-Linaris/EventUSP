//package model
//
//import br.usp.eventUSP.configureRouting
//import br.usp.eventUSP.database.tables.*
//import br.usp.eventUSP.model.Evento
//import br.usp.eventUSP.repository.EventoRepository
//import br.usp.eventUSP.repository.UsuarioOrganizadorRepository
//import br.usp.eventUSP.EventoRequest
//import br.usp.eventUSP.LoginRequest
//import br.usp.eventUSP.UserResponse
//import br.usp.eventUSP.LoginResponse
//import br.usp.eventUSP.model.UsuarioOrganizador
//import br.usp.eventUSP.model.UsuarioParticipante
//import io.ktor.server.testing.*
//import io.ktor.client.request.*
//import io.ktor.client.request.forms.formData
//import io.ktor.client.request.forms.submitFormWithBinaryData
//import io.ktor.client.statement.*
//import io.ktor.http.*
//import kotlinx.serialization.*
//import kotlinx.serialization.json.*
//import org.jetbrains.exposed.sql.*
//import org.jetbrains.exposed.sql.transactions.transaction
//import org.jetbrains.exposed.sql.SchemaUtils
//import org.junit.jupiter.api.*
//import kotlin.test.assertEquals
//import kotlin.test.assertNotNull
//import java.time.LocalDateTime
//
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//class LikeRouteTest {
//
//    // Lista de todas as tabelas na ordem correta para criação/exclusão.
//    private val allTables = arrayOf(
//        UsuarioOrganizadorTable,
//        UsuarioParticipanteTable,
//        EventoTable,
//        ImagemEventoTable,
//        ReviewTable,
//        ParticipantesInteressadosTable
//    )
//
//    @BeforeAll
//    fun setupDatabase() {
//        // Conecta ao banco de dados em memória uma única vez.
//        Database.connect(
//            "jdbc:h2:mem:test-like;DB_CLOSE_DELAY=-1;",
//            driver = "org.h2.Driver",
//            user = "root",
//            password = ""
//        )
//
//        // Cria o schema uma única vez.
//        transaction {
//            SchemaUtils.create(*allTables)
//        }
//    }
//
//    // LIMPEZA ANTES DE CADA TESTE: Roda antes de cada @Test.
//    @BeforeEach
//    fun cleanTables() {
//        transaction {
//            // A ordem de exclusão é a inversa da criação para respeitar as chaves estrangeiras.
//            allTables.reversedArray().forEach { table ->
//                table.deleteAll()
//            }
//        }
//    }
//
//    // DESMONTAGEM ÚNICA: Roda apenas uma vez depois de todos os testes da classe.
//    @AfterAll
//    fun tearDownDatabase() {
//        // Boa prática para limpar os recursos, embora o H2 em memória seja descartado de qualquer maneira.
//        transaction {
//            SchemaUtils.drop(*allTables.reversedArray())
//        }
//    }
//
//    @Test
//    @Timeout(10)
//    fun `deve demonstrar interesse em um evento e retornar corretamente`() {
//
//        val json = Json { ignoreUnknownKeys = true }
//
//        testApplication {
//            application { configureRouting() }
//
//            // Cria organizador usando multipart/form-data
//            val createResponse = client.submitFormWithBinaryData(
//                url = "/api/users/register",
//                formData = formData {
//                    append("email", "org3@usp.br")
//                    append("username", "Organizador Que Cria Evento")
//                    append("password", "orgpass")
//                    append("accountType", "organizador")
//                    append("profilePhoto","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSABqV-VQBSF1Qyj1Qyo6RiLfpA0McI1leTvQ&s")
//                }
//            )
//
//            assertEquals(HttpStatusCode.Created, createResponse.status)
//
//            val organizerResponse = json.decodeFromString<UserResponse<UsuarioOrganizador>>(createResponse.bodyAsText())
//
//            // Faz login com o organizador para criar o evento
//            val loginRequest1 = LoginRequest("org3@usp.br", "orgpass")
//            val loginResponse1 = client.post("/api/users/login") {
//                contentType(ContentType.Application.Json)
//                setBody(json.encodeToString(loginRequest1))
//            }
//            assertEquals(HttpStatusCode.OK, loginResponse1.status, "O login do organizador falhou")
//
//            val loginData1 = Json.decodeFromString<LoginResponse<UsuarioOrganizador>>(loginResponse1.bodyAsText())
//            val tokenOrganizador = loginData1.token
//            assertNotNull(tokenOrganizador)
//
//            val eventoRequest = EventoRequest(
//                titulo = "Festa de Integração",
//                descricao = "Evento para integração dos alunos",
//                dataHora = LocalDateTime.now().plusDays(3).toString(), // Formato ISO-8601
//                localizacao = "USP Butantã",
//                categoria = "Festa",
//                organizadorId = organizerResponse.user.id!!
//            )
//
//            // Envia requisição para criar evento
//            val response = client.post("/api/eventos") {
//                contentType(ContentType.Application.Json)
//                setBody(json.encodeToString(eventoRequest))
//                header(HttpHeaders.Authorization, "Bearer $tokenOrganizador")
//            }
//
//            println(response.status)
//
//            assertEquals(HttpStatusCode.Companion.Created, response.status)
//
//            // Pega o evento criado na resposta
//            val createdEvento = json.decodeFromString<Evento>(response.bodyAsText())
//            val eventRepo = EventoRepository()
//            val salvo = eventRepo.findById(createdEvento.id!!)
//            assertNotNull(salvo)
//
//            // --- REGISTRAR UM PARTICIPANTE ---
//            val registerResponse = client.submitFormWithBinaryData(
//                url = "/api/users/register",
//                formData = formData {
//                    append("email", "participantelike@test.com")
//                    append("username", "Like Tester")
//                    append("password", "senha123")
//                    append("accountType", "participante")
//                }
//            )
//            assertEquals(HttpStatusCode.Created, registerResponse.status, "O registro do participante falhou")
//
//            // --- FAZER LOGIN COM O NOVO PARTICIPANTE PARA OBTER O TOKEN ---
//            val loginRequest = LoginRequest("participantelike@test.com", "senha123")
//            val loginResponse = client.post("/api/users/login") {
//                contentType(ContentType.Application.Json)
//                setBody(json.encodeToString(loginRequest))
//            }
//            assertEquals(HttpStatusCode.OK, loginResponse.status, "O login do participante falhou")
//
//            // Extrair o token da resposta do login
//            val loginData = Json.decodeFromString<LoginResponse<UsuarioParticipante>>(loginResponse.bodyAsText())
//            val token = loginData.token
//            val participanteId = loginData.user.id
//            assertNotNull(token)
//            assertNotNull(participanteId)
//
//            // --- FAZER A REQUISIÇÃO DE INTERESSE COM O TOKEN OBTIDO ---
//            val interesseResponse = client.post("/api/eventos/${salvo.id}/interesse") {
//                header(HttpHeaders.Authorization, "Bearer $token")
//            }
//
//            // --- VERIFICAR O RESULTADO ---
//            assertEquals(HttpStatusCode.Created, interesseResponse.status, "A requisição de interesse falhou")
//
//            // Verificar o estado do banco de dados
//
//            transaction {
//                val interestEntry = ParticipantesInteressadosTable.select {
//                    (ParticipantesInteressadosTable.eventoId eq salvo.id!!) and
//                            (ParticipantesInteressadosTable.participanteId eq participanteId)
//                }.singleOrNull()
//
//                assertNotNull(interestEntry, "A entrada de interesse não foi criada na tabela de junção.")
//
//                val eventoRepository = EventoRepository()
//                val updatedEvent = eventoRepository.findById(salvo.id!!)
//                assertNotNull(updatedEvent)
//                assertEquals(1, updatedEvent.participantesInteressados.size, "O número de interessados não foi incrementado.")
//                assertEquals(participanteId, updatedEvent.participantesInteressados[0].id)
//            }
//        }
//    }
//
//}

package model

import br.usp.eventUSP.configureRouting
import br.usp.eventUSP.database.tables.*
import br.usp.eventUSP.repository.EventoRepository
import br.usp.eventUSP.EventoRequest
import br.usp.eventUSP.model.Evento
import br.usp.eventUSP.LoginRequest
import br.usp.eventUSP.LoginResponse
import br.usp.eventUSP.RegisterRequest
import br.usp.eventUSP.UserResponse
import br.usp.eventUSP.model.UsuarioOrganizador
import br.usp.eventUSP.model.UsuarioParticipante
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SchemaUtils
import org.junit.jupiter.api.*
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LikeRouteTest {

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
        Database.connect(
            "jdbc:h2:mem:test-like;DB_CLOSE_DELAY=-1;",
            driver = "org.h2.Driver",
            user = "root",
            password = ""
        )
        transaction { SchemaUtils.create(*allTables) }
    }

    @BeforeEach
    fun cleanTables() {
        transaction {
            allTables.reversedArray().forEach { table ->
                table.deleteAll()
            }
        }
    }

    @AfterAll
    fun tearDownDatabase() {
        transaction { SchemaUtils.drop(*allTables.reversedArray()) }
    }

    @Test
    @Timeout(10)
    fun `deve demonstrar interesse em um evento e retornar corretamente`() = testApplication {
        application { configureRouting() }
        val json = Json { ignoreUnknownKeys = true }

        // --- MODIFICADO: Cria organizador via JSON ---
        val registerRequestOrg = RegisterRequest(
            email = "org3@usp.br",
            username = "Organizador Que Cria Evento",
            password = "orgpass",
            accountType = "organizador",
            profilePhoto = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSABqV-VQBSF1Qyj1Qyo6RiLfpA0McI1leTvQ&s"
        )
        val createResponse = client.post("/api/users/register") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(registerRequestOrg))
        } //
        assertEquals(HttpStatusCode.Created, createResponse.status)

        val organizerResponse = json.decodeFromString<UserResponse<UsuarioOrganizador>>(createResponse.bodyAsText())

        // Login do organizador para obter o token
        val loginRequest1 = LoginRequest("org3@usp.br", "orgpass")
        val loginResponse1 = client.post("/api/users/login") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(loginRequest1))
        }
        assertEquals(HttpStatusCode.OK, loginResponse1.status)
        val loginData1 = json.decodeFromString<LoginResponse<UsuarioOrganizador>>(loginResponse1.bodyAsText())
        val tokenOrganizador = loginData1.token

        // Criação do Evento
        val eventoRequest = EventoRequest(
            titulo = "Festa de Integração",
            descricao = "Evento para integração dos alunos",
            dataHora = LocalDateTime.now().plusDays(3).toString(),
            localizacao = "USP Butantã",
            categoria = "Festa",
            organizadorId = organizerResponse.user.id!!
        )
        val response = client.post("/api/eventos") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(eventoRequest))
            header(HttpHeaders.Authorization, "Bearer $tokenOrganizador")
        }
        assertEquals(HttpStatusCode.Created, response.status)
        val createdEvento = json.decodeFromString<Evento>(response.bodyAsText())

        // --- MODIFICADO: Cria participante via JSON ---
        val registerRequestPart = RegisterRequest(
            email = "participantelike@test.com",
            username = "Like Tester",
            password = "senha123",
            accountType = "participante",
            profilePhoto = null
        )
        val registerResponse = client.post("/api/users/register") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(registerRequestPart))
        } //
        assertEquals(HttpStatusCode.Created, registerResponse.status)

        // Login do participante para obter o token
        val loginRequest = LoginRequest("participantelike@test.com", "senha123")
        val loginResponse = client.post("/api/users/login") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(loginRequest))
        }
        assertEquals(HttpStatusCode.OK, loginResponse.status)
        val loginData = json.decodeFromString<LoginResponse<UsuarioParticipante>>(loginResponse.bodyAsText())
        val tokenParticipante = loginData.token
        val participanteId = loginData.user.id!!

        // Requisição de interesse
        val interesseResponse = client.post("/api/eventos/${createdEvento.id!!}/interesse") {
            header(HttpHeaders.Authorization, "Bearer $tokenParticipante")
        }
        assertEquals(HttpStatusCode.Created, interesseResponse.status)

        // Validação no banco de dados
        transaction {
            val interestEntry = ParticipantesInteressadosTable.select {
                (ParticipantesInteressadosTable.eventoId eq createdEvento.id!!) and
                        (ParticipantesInteressadosTable.participanteId eq participanteId)
            }.singleOrNull()
            assertNotNull(interestEntry, "A entrada de interesse não foi criada.")

            val eventoRepository = EventoRepository()
            val updatedEvent = eventoRepository.findById(createdEvento.id!!)
            assertNotNull(updatedEvent)
            assertEquals(1, updatedEvent.participantesInteressados.size)
        }
    }
}
