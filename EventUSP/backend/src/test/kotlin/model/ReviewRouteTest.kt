package model

import br.usp.eventUSP.configureRouting
import br.usp.eventUSP.database.tables.*
import br.usp.eventUSP.model.Evento
import br.usp.eventUSP.repository.EventoRepository
import br.usp.eventUSP.repository.UsuarioOrganizadorRepository
import br.usp.eventUSP.EventoRequest
import br.usp.eventUSP.LoginRequest
import br.usp.eventUSP.UserResponse
import br.usp.eventUSP.LoginResponse
import br.usp.eventUSP.ReviewRequest
import br.usp.eventUSP.model.UsuarioOrganizador
import br.usp.eventUSP.model.UsuarioParticipante
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
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import java.time.LocalDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReviewRouteTest {

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
            "jdbc:h2:mem:test-review;DB_CLOSE_DELAY=-1;",
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
    fun `deve criar review em um evento corretamente`() {

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
                titulo = "Evento de Apresentação do MESSI",
                descricao = "Lionel Messi seja bem-vindo ao botafogo",
                dataHora = LocalDateTime.now().minusDays(1).toString(), // Formato ISO-8601
                localizacao = "Engenhão (Estádio Nilton Santos)",
                categoria = "Festa",
                organizadorId = organizerResponse.user.id!!
            )

            // Envia requisição para criar evento
            val response = client.post("/api/eventos") {
                contentType(ContentType.Application.Json)
                setBody(json.encodeToString(eventoRequest))
                header(HttpHeaders.Authorization, "Bearer $tokenOrganizador")
            }

            println(response.status)

            assertEquals(HttpStatusCode.Companion.Created, response.status)

            // Pega o evento criado na resposta
            val createdEvento = json.decodeFromString<Evento>(response.bodyAsText())
            val eventRepo = EventoRepository()
            val salvo = eventRepo.findById(createdEvento.id!!)
            assertNotNull(salvo)

            // --- REGISTRAR UM PARTICIPANTE ---
            val registerResponse = client.submitFormWithBinaryData(
                url = "/api/users/register",
                formData = formData {
                    append("email", "participantereview@test.com")
                    append("username", "Review Tester")
                    append("password", "senha123")
                    append("accountType", "participante")
                }
            )
            assertEquals(HttpStatusCode.Created, registerResponse.status, "O registro do participante falhou")

            // --- FAZER LOGIN COM O NOVO PARTICIPANTE PARA OBTER O TOKEN ---
            val loginRequest = LoginRequest("participantereview@test.com", "senha123")
            val loginResponse = client.post("/api/users/login") {
                contentType(ContentType.Application.Json)
                setBody(json.encodeToString(loginRequest))
            }
            assertEquals(HttpStatusCode.OK, loginResponse.status, "O login do participante falhou")

            // Extrair o token da resposta do login
            val loginData = Json.decodeFromString<LoginResponse<UsuarioParticipante>>(loginResponse.bodyAsText())
            val tokenParticipante = loginData.token
            val participanteId = loginData.user.id
            assertNotNull(tokenParticipante)
            assertNotNull(participanteId)

            // --- FAZER A REQUISIÇÃO DE INTERESSE COM O TOKEN OBTIDO ---
            val interesseResponse = client.post("/api/eventos/${salvo.id}/interesse") {
                header(HttpHeaders.Authorization, "Bearer $tokenParticipante")
            }

            // --- VERIFICAR O RESULTADO ---
            assertEquals(HttpStatusCode.Created, interesseResponse.status, "A requisição de interesse falhou")

            // Pegar o evento atualizado no banco de dados
            val eventoRepository = EventoRepository()
            val eventoComInteresse = eventoRepository.findById(salvo.id!!)
            assertNotNull(eventoComInteresse)

            // --- FAZER A REQUISIÇÃO DE REVIEW COM O TOKEN OBTIDO ---
            val reviewRequest = ReviewRequest(4, "Faltou o messi")
            val reviewResponse = client.post("/api/eventos/${eventoComInteresse.id}/reviews") {
                contentType(ContentType.Application.Json)
                setBody(json.encodeToString(reviewRequest))
                header(HttpHeaders.Authorization, "Bearer $tokenParticipante")
            }

            // --- VERIFICAR O RESULTADO ---
            assertEquals(HttpStatusCode.Created, reviewResponse.status, "A requisição de review falhou")

            // Verificar o estado do banco de dados

            transaction {
                val reviewRow = ReviewTable.select {
                    (ReviewTable.eventoId eq salvo.id!!) and
                            (ReviewTable.participanteId eq participanteId)
                }.singleOrNull()

                assertNotNull(reviewRow, "A entrada da review não foi criada na tabela.")
                assertEquals(4, reviewRow[ReviewTable.nota])
                assertEquals("Faltou o messi", reviewRow[ReviewTable.comentario])
            }

            val eventoAtualizado = eventoRepository.findById(eventoComInteresse.id!!)
            assertNotNull(eventoAtualizado)
            assertEquals(1, eventoAtualizado.reviews.size, "O número de reviews não foi incrementada.")

            val reviewNoModelo = eventoAtualizado.reviews[0]
            assertEquals(participanteId, reviewNoModelo.participanteId)
            assertEquals(4, reviewNoModelo.nota)
            assertEquals("Faltou o messi", reviewNoModelo.comentario)

        }
    }

}