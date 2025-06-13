package br.usp.eventUSP.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDateTime
import br.usp.eventUSP.database.tables.UsuarioOrganizadorTable
import br.usp.eventUSP.database.tables.UsuarioParticipanteTable
import br.usp.eventUSP.repository.UsuarioOrganizadorRepository
import br.usp.eventUSP.repository.UsuarioParticipanteRepository
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.deleteAll

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UsuarioParticipanteTest {
    
    private lateinit var participante: UsuarioParticipante
    private lateinit var participanteSemFoto: UsuarioParticipante
    private lateinit var evento: Evento
    private lateinit var organizador: UsuarioOrganizador

    private lateinit var repository: UsuarioParticipanteRepository

    @BeforeAll
    fun setUpDatabase() {
        // Use um banco de dados em memória para testes!
        Database.connect(
            "jdbc:h2:mem:test-participant;DB_CLOSE_DELAY=-1;",
            driver = "org.h2.Driver"
        )
        transaction {
            SchemaUtils.create(UsuarioParticipanteTable)
        }
        repository = UsuarioParticipanteRepository() // ou como for sua inicialização
    }

    @BeforeEach
    fun cleanDatabase() {
        transaction {
            UsuarioParticipanteTable.deleteAll()
        }
    }

    @BeforeEach
    fun setup() {
        organizador = UsuarioOrganizador()
        organizador.id = 1L
        organizador.nome = "Organizador Teste"
        organizador.email = "organizador@usp.br"
        organizador.senha = "senha123"
        organizador.fotoPerfil = "https://exemplo.com/foto-organizador.jpg"
        
        participante = UsuarioParticipante(
            id = 2L,
            nome = "Participante Com Foto",
            email = "participante@usp.br",
            senha = "senha456",
            fotoPerfil = "https://exemplo.com/foto-perfil.jpg"
        )
        
        participanteSemFoto = UsuarioParticipante(
            id = 3L,
            nome = "Participante Sem Foto",
            email = "sem-foto@usp.br",
            senha = "senha789"
        )
        
        evento = Evento(
            id = 1L,
            titulo = "Evento Teste",
            descricao = "Descrição do evento",
            dataHora = LocalDateTime.now().plusDays(5),
            localizacao = "Local do Evento",
            categoria = "Categoria",
            organizador = organizador
        )
    }
    
    @Test
    fun `deve inicializar participante com foto de perfil corretamente`() {
        assertEquals(2L, participante.id)
        assertEquals("Participante Com Foto", participante.nome)
        assertEquals("participante@usp.br", participante.email)
        assertEquals("senha456", participante.senha)
        assertEquals("https://exemplo.com/foto-perfil.jpg", participante.fotoPerfil)
        assertTrue(participante.eventosComLike.isEmpty())
        assertTrue(participante.eventosInteressado.isEmpty())
        assertTrue(participante.reviewsFeitas.isEmpty())
    }
    
    @Test
    fun `deve inicializar participante sem foto de perfil corretamente`() {
        assertEquals(3L, participanteSemFoto.id)
        assertEquals("Participante Sem Foto", participanteSemFoto.nome)
        assertEquals("sem-foto@usp.br", participanteSemFoto.email)
        assertEquals("senha789", participanteSemFoto.senha)
        assertNull(participanteSemFoto.fotoPerfil)
    }
    
    @Test
    fun `deve atualizar foto de perfil com sucesso`() {
        participante.atualizarFotoPerfil("https://exemplo.com/nova-foto.jpg")
        assertEquals("https://exemplo.com/nova-foto.jpg", participante.fotoPerfil)
        
        participanteSemFoto.atualizarFotoPerfil("https://exemplo.com/primeira-foto.jpg")
        assertEquals("https://exemplo.com/primeira-foto.jpg", participanteSemFoto.fotoPerfil)
    }
    
    @Test
    fun `deve dar like em evento com sucesso`() {
        assertTrue(participante.darLike(evento))
        assertEquals(1, participante.eventosComLike.size)
        assertTrue(participante.eventosComLike.contains(evento))
        assertEquals(1, evento.numeroLikes)
    }
    
    @Test
    fun `não deve dar like duplicado no mesmo evento`() {
        participante.darLike(evento)
        assertFalse(participante.darLike(evento))
        assertEquals(1, participante.eventosComLike.size)
        assertEquals(1, evento.numeroLikes)
    }
    
    @Test
    fun `deve remover like de evento com sucesso`() {
        participante.darLike(evento)
        assertTrue(participante.removerLike(evento))
        assertEquals(0, participante.eventosComLike.size)
        assertEquals(0, evento.numeroLikes)
    }
    
    @Test
    fun `não deve remover like de evento que não recebeu like`() {
        assertFalse(participante.removerLike(evento))
        assertEquals(0, evento.numeroLikes)
    }
    
    @Test
    fun `deve demonstrar interesse em evento com sucesso`() {
        assertTrue(participante.demonstrarInteresse(evento))
        assertEquals(1, participante.eventosInteressado.size)
        assertTrue(participante.eventosInteressado.contains(evento))
        assertEquals(1, evento.participantesInteressados.size)
        assertTrue(evento.participantesInteressados.contains(participante))
    }
    
    @Test
    fun `não deve demonstrar interesse duplicado no mesmo evento`() {
        participante.demonstrarInteresse(evento)
        assertFalse(participante.demonstrarInteresse(evento))
        assertEquals(1, participante.eventosInteressado.size)
        assertEquals(1, evento.participantesInteressados.size)
    }
    
    @Test
    fun `deve remover interesse em evento com sucesso`() {
        participante.demonstrarInteresse(evento)
        assertTrue(participante.removerInteresse(evento))
        assertEquals(0, participante.eventosInteressado.size)
        assertEquals(0, evento.participantesInteressados.size)
    }
    
    @Test
    fun `não deve remover interesse de evento que não demonstrou interesse`() {
        assertFalse(participante.removerInteresse(evento))
    }
    
    @Test
    fun `deve comparar participantes corretamente pelo id ou email`() {
        val participanteMesmoId = UsuarioParticipante(
            id = 2L,
            nome = "Outro Nome",
            email = "outro@usp.br",
            senha = "outrasenha"
        )
        
        val participanteMesmoEmail = UsuarioParticipante(
            id = 99L,
            nome = "Outro Nome",
            email = "participante@usp.br",
            senha = "outrasenha"
        )
        
        val participanteDiferente = UsuarioParticipante(
            id = 100L,
            nome = "Participante Diferente",
            email = "diferente@usp.br",
            senha = "senha123"
        )
        
        assertEquals(participante, participanteMesmoId)
        assertEquals(participante, participanteMesmoEmail)
        assertNotEquals(participante, participanteDiferente)
    }

    @Test
    fun `deve criar participante e persistir no banco de dados`() {
        // Coloca o participante criado no repositório
        repository.create(participante)

        // Agora consultamos direto na tabela para garantir que persistiu
        val participanteNoBanco = transaction {
            UsuarioParticipanteTable.select { UsuarioParticipanteTable.email eq participante.email }.singleOrNull()
        }

        assertNotNull(participanteNoBanco)
        assertEquals(participante.nome, participanteNoBanco?.get(UsuarioParticipanteTable.nome))
        assertEquals(participante.email, participanteNoBanco?.get(UsuarioParticipanteTable.email))
        assertEquals(participante.senha, participanteNoBanco?.get(UsuarioParticipanteTable.senha))
    }
}
