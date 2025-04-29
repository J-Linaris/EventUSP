package br.usp.eventUSP.model
package br.usp.eventUSP.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDateTime

class UsuarioParticipanteTest {
    
    private lateinit var participante: UsuarioParticipante
    private lateinit var participanteSemFoto: UsuarioParticipante
    private lateinit var evento: Evento
    private lateinit var organizador: UsuarioOrganizador
    
    @BeforeEach
    fun setup() {
        organizador = UsuarioOrganizador(
            id = 1L,
            nome = "Organizador Teste",
            email = "organizador@usp.br",
            senha = "senha123",
            cpf = "12345678901",
            instituicao = "ICMC"
        )
        
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
            imagemCapa = "https://exemplo.com/imagem.jpg",
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
}
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
