package model

import br.usp.eventUSP.configureRouting
import br.usp.eventUSP.database.tables.*
import br.usp.eventUSP.model.Evento
import br.usp.eventUSP.repository.EventoRepository
import br.usp.eventUSP.repository.UsuarioOrganizadorRepository
import br.usp.eventUSP.EventoRequest
import br.usp.eventUSP.UserResponse
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
        // Agora, em vez de apagar e recriar as tabelas (lento),
        // nós apenas deletamos todos os registros (muito mais rápido).
        transaction {
            // A ordem de exclusão é a inversa da criação para respeitar as chaves estrangeiras.
            // Ex: Deleta 'Review' antes de 'Evento', que deleta antes de 'Usuario'.
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
                    val arquivoFoto = File("src/test/kotlin/bemvindomessi.jpeg")
                    append(
                        "profilePhoto",
                        arquivoFoto.readBytes(),
                        Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                            append(HttpHeaders.ContentDisposition, "filename=\"bemvindomessi.jpg\"")
                        }
                    )
                }
            )

            println(createResponse.status)
            println(createResponse.bodyAsText())
            assertEquals(HttpStatusCode.Created, createResponse.status)

            val organizerResponse = json.decodeFromString<UserResponse<UsuarioOrganizador>>(createResponse.bodyAsText())
            assertEquals("Organizador criado com sucesso", organizerResponse.message)
            assertEquals("Organizador Que Cria Evento", organizerResponse.user.nome)
            // assertEquals("https://linkfoto.com/foto.jpg", organizerResponse.user.fotoPerfil)
            assertNotNull(organizerResponse.token)

            val eventoRequest = EventoRequest(
                titulo = "Festa de Integração",
                descricao = "Evento para integração dos alunos",
                dataHora = LocalDateTime.now().plusDays(3).toString(), // Formato ISO-8601
                localizacao = "USP Butantã",
                categoria = "Festa",
                organizadorId = organizerResponse.user.id!!
            )

            // Envia requisição para criar evento
            val response = client.post("/api/eventos/criar") {
                contentType(ContentType.Application.Json)
                setBody(json.encodeToString(eventoRequest))
            }

            println(response.status)

            assertEquals(HttpStatusCode.Companion.Created, response.status)

            // Valida se evento foi criado na resposta
            val createdEvento = json.decodeFromString<Evento>(response.bodyAsText())
            assertEquals(eventoRequest.titulo, createdEvento.titulo)
            assertEquals(eventoRequest.descricao, createdEvento.descricao)
            assertEquals(eventoRequest.localizacao, createdEvento.localizacao)
            assertEquals(eventoRequest.organizadorId, createdEvento.organizador.id)
            // assertEquals(1, createdEvento.imagens.size)
            // assertEquals("https://link.com/img.jpg", createdEvento.imagens[0].url)

            // Valida se evento foi salvo no banco (repositório)
            val eventRepo = EventoRepository()
            val salvo = eventRepo.findById(createdEvento.id!!)
            assertNotNull(salvo)
            assertEquals(eventoRequest.titulo, salvo.titulo)
            assertEquals(eventoRequest.organizadorId, salvo.organizador.id)
            //   assertTrue(salvo.imagens.any { it.url == "https://link.com/img.jpg" } ?: false)
        }
    }
}