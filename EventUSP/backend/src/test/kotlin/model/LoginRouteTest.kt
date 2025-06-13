package br.usp.eventUSP

import br.usp.eventUSP.database.tables.UsuarioOrganizadorTable
import br.usp.eventUSP.database.tables.UsuarioParticipanteTable
import br.usp.eventUSP.model.UsuarioOrganizador
import br.usp.eventUSP.model.UsuarioParticipante
import io.ktor.server.testing.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoginRouteTest {

    @BeforeAll
    fun prepareDatabase() {
        Database.connect(
            "jdbc:h2:mem:test-login-signup;DB_CLOSE_DELAY=-1;",
            driver = "org.h2.Driver"
        )
        transaction {
            SchemaUtils.create(UsuarioOrganizadorTable)
            SchemaUtils.create(UsuarioParticipanteTable)
        }
    }

    @BeforeEach
    fun cleanTables() {
        transaction {
            // Limpa as tabelas antes de cada teste
            UsuarioOrganizadorTable.deleteAll()
            UsuarioParticipanteTable.deleteAll()
        }
    }

    @Test
    fun `deve criar conta de participante e realizar login`() = testApplication {
        application { configureRouting() }

        val json = Json { ignoreUnknownKeys = true }

        // Cria participante usando multipart/form-data
        val createResponse = client.submitFormWithBinaryData(
            url = "/api/users/register",
            formData = formData {
                append("email", "participante123@usp.br")
                append("username", "Usuário Participante")
                append("password", "senhaSegura")
                append("accountType", "participante")
            }
        )

        println(createResponse.status)
        println(createResponse.bodyAsText())
        assertEquals(HttpStatusCode.Created, createResponse.status)

        val participantResponse = json.decodeFromString<UserResponse<UsuarioParticipante>>(createResponse.bodyAsText())
        assertEquals("Participante criado com sucesso", participantResponse.message)
        assertEquals("participante123@usp.br", participantResponse.user.email)
        assertEquals("Usuário Participante", participantResponse.user.nome)
        assertNotNull(participantResponse.token)

        // Login participante
        val loginRequest = LoginRequest("participante123@usp.br", "senhaSegura")
        val loginResp = client.post("/api/users/login") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(loginRequest))
        }
        assertEquals(HttpStatusCode.OK, loginResp.status)

        val loginResponse = json.decodeFromString<LoginResponse<UsuarioParticipante>>(loginResp.bodyAsText())
        assertEquals("Login efetuado com sucesso!", loginResponse.message)
        assertEquals("Usuário Participante", loginResponse.user.nome)
        assertEquals("participante123@usp.br", loginResponse.user.email)
    }

    @Test
    fun `deve criar conta de organizador e realizar login`() = testApplication {
        application { configureRouting() }

        val json = Json { ignoreUnknownKeys = true }

        // Cria organizador usando multipart/form-data
        val createResponse = client.submitFormWithBinaryData(
            url = "/api/users/register",
            formData = formData {
                append("email", "org123@usp.br")
                append("username", "Organizador Evento")
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
        assertEquals("Organizador Evento", organizerResponse.user.nome)
       // assertEquals("https://linkfoto.com/foto.jpg", organizerResponse.user.fotoPerfil)
        assertNotNull(organizerResponse.token)

        // Login organizador
        val loginRequest = LoginRequest("org123@usp.br", "orgpass")
        val loginResp = client.post("/api/users/login") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(loginRequest))
        }
        assertEquals(HttpStatusCode.OK, loginResp.status)

        val loginResponse = json.decodeFromString<LoginResponse<UsuarioOrganizador>>(loginResp.bodyAsText())
        assertEquals("Login efetuado com sucesso!", loginResponse.message)
        assertEquals("Organizador Evento", loginResponse.user.nome)
        assertEquals("org123@usp.br", loginResponse.user.email)
      //  assertEquals("https://linkfoto.com/foto.jpg", loginResponse.user.fotoPerfil)
    }

    @Test
    fun `login de participante com senha errada deve falhar`() = testApplication {
        application { configureRouting() }
        val json = Json { ignoreUnknownKeys = true }


        // Cadastro prévio usando multipart/form-data
        val createResponse = client.submitFormWithBinaryData(
            url = "/api/users/register",
            formData = formData {
                append("email", "p2@usp.br")
                append("username", "Participante B")
                append("password", "senhaCorreta")
                append("accountType", "participante")
            }
        )
        assertEquals(HttpStatusCode.Created, createResponse.status)

        // Login errado
        val loginRequest = LoginRequest("p2@usp.br", "senhaIncorreta")
        val loginResp = client.post("/api/users/login") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(loginRequest))
        }
        assertEquals(HttpStatusCode.Unauthorized, loginResp.status)
    }
}