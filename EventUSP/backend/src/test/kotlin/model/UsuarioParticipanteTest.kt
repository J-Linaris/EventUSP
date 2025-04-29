package br.usp.eventUSP.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDateTime

class UsuarioParticipanteTest {
    
    private lateinit var participante: UsuarioParticipante
    private lateinit var organizador: UsuarioOrganizador
    private lateinit var evento1: Evento
    private lateinit var evento2: Evento
    
    @BeforeEach
    fun setup() {
        participante = UsuarioParticipante(
            id = 1L,
            nome = "João Silva",
            email = "joao.silva@usp.br",
            senha = "senha123"
        )
        
        organizador = UsuarioOrganizador(
            id = 2L,
            nome = "Maria Organizadora",
            email = "maria@usp.br",
            senha = "senha456",
            cpf = "98765432101",
            instituicao = "ICMC"
        )
        
        evento1 = Evento(
            id = 1L,
            titulo = "Palestra de IA",
            descricao = "Palestra sobre Inteligência Artificial",
            dataHora = LocalDateTime.now().plusDays(5),
            localizacao = "Auditório ICMC",
            imagem = "https://exemplo.com/ia.jpg",
            categoria = "Tecnologia",
            capacidadeMaxima = 100,
            organizador = organizador
        )
        
        evento2 = Evento(
            id = 2L,
            titulo = "Workshop de Programação",
            descricao = "Workshop prático de programação",
            dataHora = LocalDateTime.now().plusDays(10),
            localizacao = "Laboratório ICMC",
            imagem = "https://exemplo.com/workshop.jpg",
            categoria = "Programação",
            capacidadeMaxima = 30,
            organizador = organizador
        )
    }
    
    @Test
    fun `deve inicializar usuário participante corretamente`() {
        assertEquals(1L, participante.id)
        assertEquals("João Silva", participante.nome)
        assertEquals("joao.silva@usp.br", participante.email)
        assertEquals("senha123", participante.senha)
        assertTrue(participante.eventosComLike.isEmpty())
        assertTrue(participante.eventosInteressado.isEmpty())
    }
    
    @Test
    fun `deve dar like em evento com sucesso`() {
        assertTrue(participante.darLike(evento1))
        assertEquals(1, participante.eventosComLike.size)
        assertTrue(participante.eventosComLike.contains(evento1))
        assertEquals(1, evento1.numeroLikes)
    }
    
    @Test
    fun `não deve permitir dar like duplicado em mesmo evento`() {
        participante.darLike(evento1)
        assertFalse(participante.darLike(evento1))
        assertEquals(1, participante.eventosComLike.size)
        assertEquals(1, evento1.numeroLikes)
    }
    
    @Test
    fun `deve remover like de evento com sucesso`() {
        participante.darLike(evento1)
        assertEquals(1, evento1.numeroLikes)
        
        assertTrue(participante.removerLike(evento1))
        assertEquals(0, participante.eventosComLike.size)
        assertEquals(0, evento1.numeroLikes)
    }
    
    @Test
    fun `não deve permitir remover like de evento que não tem like`() {
        assertFalse(participante.removerLike(evento1))
        assertEquals(0, evento1.numeroLikes)
    }
    
    @Test
    fun `deve demonstrar interesse em evento com sucesso`() {
        assertTrue(participante.demonstrarInteresse(evento1))
        assertEquals(1, participante.eventosInteressado.size)
        assertTrue(participante.eventosInteressado.contains(evento1))
        assertTrue(evento1.participantesInteressados.contains(participante))
    }
    
    @Test
    fun `não deve permitir demonstrar interesse duplicado em mesmo evento`() {
        participante.demonstrarInteresse(evento1)
        assertFalse(participante.demonstrarInteresse(evento1))
        assertEquals(1, participante.eventosInteressado.size)
        assertEquals(1, evento1.participantesInteressados.size)
    }
    
    @Test
    fun `deve remover interesse em evento com sucesso`() {
        participante.demonstrarInteresse(evento1)
        assertEquals(1, evento1.participantesInteressados.size)
        
        assertTrue(participante.removerInteresse(evento1))
        assertEquals(0, participante.eventosInteressado.size)
        assertEquals(0, evento1.participantesInteressados.size)
    }
    
    @Test
    fun `não deve permitir remover interesse de evento que não há interesse`() {
        assertFalse(participante.removerInteresse(evento1))
        assertEquals(0, evento1.participantesInteressados.size)
    }
    
    @Test
    fun `deve permitir dar like e ter interesse em múltiplos eventos`() {
        participante.darLike(evento1)
        participante.darLike(evento2)
        participante.demonstrarInteresse(evento1)
        participante.demonstrarInteresse(evento2)
        
        assertEquals(2, participante.eventosComLike.size)
        assertEquals(2, participante.eventosInteressado.size)
        assertEquals(1, evento1.numeroLikes)
        assertEquals(1, evento2.numeroLikes)
        assertEquals(1, evento1.participantesInteressados.size)
        assertEquals(1, evento2.participantesInteressados.size)
    }
    
    @Test
    fun `deve comparar usuários corretamente pelo id e email`() {
        val participanteMesmoId = UsuarioParticipante(
            id = 1L,
            nome = "Outro Nome",
            email = "outro@usp.br",
            senha = "outrasenha"
        )
        
        val participanteMesmoEmail = UsuarioParticipante(
            id = 3L,
            nome = "Outro Nome",
            email = "joao.silva@usp.br",
            senha = "outrasenha"
        )
        
        val participanteDiferente = UsuarioParticipante(
            id = 4L,
            nome = "Pedro Santos",
            email = "pedro@usp.br",
            senha = "senha789"
        )
        
        assertEquals(participante, participanteMesmoId)
        assertEquals(participante, participanteMesmoEmail)
        assertNotEquals(participante, participanteDiferente)
    }
}
