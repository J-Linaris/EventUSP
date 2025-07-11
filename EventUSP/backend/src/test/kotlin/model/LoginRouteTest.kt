//package br.usp.eventUSP
//
//import br.usp.eventUSP.database.tables.*
//import br.usp.eventUSP.model.UsuarioOrganizador
//import br.usp.eventUSP.model.UsuarioParticipante
//import io.ktor.server.testing.*
//import io.ktor.client.request.*
//import io.ktor.client.request.forms.*
//import io.ktor.client.statement.*
//import io.ktor.http.*
//import kotlinx.serialization.*
//import kotlinx.serialization.json.*
//import org.jetbrains.exposed.sql.*
//import org.jetbrains.exposed.sql.transactions.transaction
//import org.jetbrains.exposed.sql.SchemaUtils
//import org.junit.jupiter.api.*
//import java.io.File
//import kotlin.test.assertEquals
//import kotlin.test.assertNotNull
//
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//class LoginRouteTest {
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
//    // SETUP ÚNICO: Roda apenas uma vez antes de todos os testes da classe.
//    @BeforeAll
//    fun setupDatabase() {
//        // Conecta ao banco de dados em memória uma única vez.
//        Database.connect(
//            "jdbc:h2:mem:test-login-signup;DB_CLOSE_DELAY=-1;",
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
//        // Agora, em vez de apagar e recriar as tabelas (lento),
//        // nós apenas deletamos todos os registros (muito mais rápido).
//        transaction {
//            // A ordem de exclusão é a inversa da criação para respeitar as chaves estrangeiras.
//            // Ex: Deleta 'Review' antes de 'Evento', que deleta antes de 'Usuario'.
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
//    fun `deve criar conta de participante e realizar login`() = testApplication {
//        application { configureRouting() }
//
//        val json = Json { ignoreUnknownKeys = true }
//
//        // Cria participante usando multipart/form-data
//        val createResponse = client.submitFormWithBinaryData(
//            url = "/api/users/register",
//            formData = formData {
//                append("email", "participante123@usp.br")
//                append("username", "Usuário Participante")
//                append("password", "senhaSegura")
//                append("accountType", "participante")
//            }
//        )
//
//        println(createResponse.status)
//        println(createResponse.bodyAsText())
//        assertEquals(HttpStatusCode.Created, createResponse.status)
//
//        val participantResponse = json.decodeFromString<UserResponse<UsuarioParticipante>>(createResponse.bodyAsText())
//        assertEquals("Participante criado com sucesso", participantResponse.message)
//        assertEquals("participante123@usp.br", participantResponse.user.email)
//        assertEquals("Usuário Participante", participantResponse.user.nome)
//        assertNotNull(participantResponse.token)
//
//        // Login participante
//        val loginRequest = LoginRequest("participante123@usp.br", "senhaSegura")
//        val loginResp = client.post("/api/users/login") {
//            contentType(ContentType.Application.Json)
//            setBody(json.encodeToString(loginRequest))
//        }
//        assertEquals(HttpStatusCode.OK, loginResp.status)
//
//        val loginResponse = json.decodeFromString<LoginResponse<UsuarioParticipante>>(loginResp.bodyAsText())
//        assertEquals("Login efetuado com sucesso!", loginResponse.message)
//        assertEquals("Usuário Participante", loginResponse.user.nome)
//        assertEquals("participante123@usp.br", loginResponse.user.email)
//    }
//
//    @Test
//    fun `deve criar conta de organizador e realizar login`() = testApplication {
//        application { configureRouting() }
//
//        val json = Json { ignoreUnknownKeys = true }
//
//        // Cria organizador usando multipart/form-data
//        val createResponse = client.submitFormWithBinaryData(
//            url = "/api/users/register",
//            formData = formData {
//                append("email", "org123@usp.br")
//                append("username", "Organizador Evento")
//                append("password", "orgpass")
//                append("accountType", "organizador")
////                    val arquivoFoto = File("src/test/kotlin/bemvindomessi.jpeg")
////                    append(
////                        "profilePhoto",
////                        arquivoFoto.readBytes(),
////                        Headers.build {
////                            append(HttpHeaders.ContentType, "image/jpeg")
////                            append(HttpHeaders.ContentDisposition, "filename=\"bemvindomessi.jpg\"")
////                        }
////                    )
//                append("profilePhoto","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSABqV-VQBSF1Qyj1Qyo6RiLfpA0McI1leTvQ&s")
//            }
//        )
//
//        println(createResponse.status)
//        println(createResponse.bodyAsText())
//        assertEquals(HttpStatusCode.Created, createResponse.status)
//
//        val organizerResponse = json.decodeFromString<UserResponse<UsuarioOrganizador>>(createResponse.bodyAsText())
//        assertEquals("Organizador criado com sucesso", organizerResponse.message)
//        assertEquals("Organizador Evento", organizerResponse.user.nome)
//        assertEquals("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSABqV-VQBSF1Qyj1Qyo6RiLfpA0McI1leTvQ&s", organizerResponse.user.fotoPerfil)
//        assertNotNull(organizerResponse.token)
//
//        // Login organizador
//        val loginRequest = LoginRequest("org123@usp.br", "orgpass")
//        val loginResp = client.post("/api/users/login") {
//            contentType(ContentType.Application.Json)
//            setBody(json.encodeToString(loginRequest))
//        }
//        assertEquals(HttpStatusCode.OK, loginResp.status)
//
//        val loginResponse = json.decodeFromString<LoginResponse<UsuarioOrganizador>>(loginResp.bodyAsText())
//        assertEquals("Login efetuado com sucesso!", loginResponse.message)
//        assertEquals("Organizador Evento", loginResponse.user.nome)
//        assertEquals("org123@usp.br", loginResponse.user.email)
//        assertEquals("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSABqV-VQBSF1Qyj1Qyo6RiLfpA0McI1leTvQ&s", loginResponse.user.fotoPerfil)
//    }
//
//    @Test
//    fun `login de participante com senha errada deve falhar`() = testApplication {
//        application { configureRouting() }
//        val json = Json { ignoreUnknownKeys = true }
//
//
//        // Cadastro prévio usando multipart/form-data
//        val createResponse = client.submitFormWithBinaryData(
//            url = "/api/users/register",
//            formData = formData {
//                append("email", "p2@usp.br")
//                append("username", "Participante B")
//                append("password", "senhaCorreta")
//                append("accountType", "participante")
//            }
//        )
//        assertEquals(HttpStatusCode.Created, createResponse.status)
//
//        // Login errado
//        val loginRequest = LoginRequest("p2@usp.br", "senhaIncorreta")
//        val loginResp = client.post("/api/users/login") {
//            contentType(ContentType.Application.Json)
//            setBody(json.encodeToString(loginRequest))
//        }
//        assertEquals(HttpStatusCode.Unauthorized, loginResp.status)
//    }
//}
package br.usp.eventUSP

import br.usp.eventUSP.database.tables.*
import br.usp.eventUSP.model.UsuarioOrganizador
import br.usp.eventUSP.model.UsuarioParticipante
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoginRouteTest {

    // Lista de todas as tabelas na ordem correta para criação/exclusão.
    private val allTables = arrayOf(
        UsuarioOrganizadorTable,
        UsuarioParticipanteTable,
        EventoTable,
        ImagemEventoTable,
        ReviewTable,
        ParticipantesInteressadosTable
    )

    // SETUP ÚNICO: Roda apenas uma vez antes de todos os testes da classe.
    @BeforeAll
    fun setupDatabase() {
        // Conecta ao banco de dados em memória uma única vez.
        Database.connect(
            "jdbc:h2:mem:test-login-signup;DB_CLOSE_DELAY=-1;",
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
    fun `deve criar conta de participante e realizar login`() = testApplication {
        application { configureRouting() }

        val json = Json { ignoreUnknownKeys = true }

        // --- MODIFICADO ---
        // Cria o objeto de requisição para o participante
        val registerRequest = RegisterRequest(
            email = "participante123@usp.br",
            username = "Usuário Participante",
            password = "senhaSegura",
            accountType = "participante",
            profilePhoto = null // Participantes não enviam foto
        )

        // Cria participante enviando um corpo JSON
        val createResponse = client.post("/api/users/register") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(registerRequest))
        }
        // --- FIM DA MODIFICAÇÃO ---

        println(createResponse.status)
        println(createResponse.bodyAsText())
        assertEquals(HttpStatusCode.Created, createResponse.status)

        val participantResponse = json.decodeFromString<UserResponse<UsuarioParticipante>>(createResponse.bodyAsText())
        assertEquals("Participante criado com sucesso", participantResponse.message)
        assertEquals("participante123@usp.br", participantResponse.user.email)
        assertEquals("Usuário Participante", participantResponse.user.nome)
        assertNotNull(participantResponse.token)

        // Login participante (esta parte já usava JSON e permanece igual)
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

        // --- MODIFICADO ---
        // Cria o objeto de requisição para o organizador
        val registerRequest = RegisterRequest(
            email = "org123@usp.br",
            username = "Organizador Evento",
            password = "orgpass",
            accountType = "organizador",
            profilePhoto = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSABqV-VQBSF1Qyj1Qyo6RiLfpA0McI1leTvQ&s"
        )

        // Cria organizador enviando um corpo JSON
        val createResponse = client.post("/api/users/register") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(registerRequest))
        }
        // --- FIM DA MODIFICAÇÃO ---

        println(createResponse.status)
        println(createResponse.bodyAsText())
        assertEquals(HttpStatusCode.Created, createResponse.status)

        val organizerResponse = json.decodeFromString<UserResponse<UsuarioOrganizador>>(createResponse.bodyAsText())
        assertEquals("Organizador criado com sucesso", organizerResponse.message)
        assertEquals("Organizador Evento", organizerResponse.user.nome)
        assertEquals("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSABqV-VQBSF1Qyj1Qyo6RiLfpA0McI1leTvQ&s", organizerResponse.user.fotoPerfil)
        assertNotNull(organizerResponse.token)

        // Login organizador (permanece igual)
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
        assertEquals("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSABqV-VQBSF1Qyj1Qyo6RiLfpA0McI1leTvQ&s", loginResponse.user.fotoPerfil)
    }

    @Test
    fun `login de participante com senha errada deve falhar`() = testApplication {
        application { configureRouting() }
        val json = Json { ignoreUnknownKeys = true }

        // --- MODIFICADO ---
        // Cria o objeto de requisição para o participante
        val registerRequest = RegisterRequest(
            email = "p2@usp.br",
            username = "Participante B",
            password = "senhaCorreta",
            accountType = "participante",
            profilePhoto = null
        )

        // Cadastro prévio enviando JSON
        val createResponse = client.post("/api/users/register") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(registerRequest))
        }
        // --- FIM DA MODIFICAÇÃO ---
        assertEquals(HttpStatusCode.Created, createResponse.status)

        // Login errado (permanece igual)
        val loginRequest = LoginRequest("p2@usp.br", "senhaIncorreta")
        val loginResp = client.post("/api/users/login") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(loginRequest))
        }
        assertEquals(HttpStatusCode.Unauthorized, loginResp.status)
    }
}
