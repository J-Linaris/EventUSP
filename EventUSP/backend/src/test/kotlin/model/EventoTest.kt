package br.usp.eventUSP.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class EventoTest {
    
    private lateinit var organizador: UsuarioOrganizador
    private lateinit var evento: Evento
    private lateinit var participante1: UsuarioParticipante
    private lateinit var participante2: UsuarioParticipante
    
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
        
        evento = Evento(
            id = 1L,
            titulo = "Workshop de Kotlin",
            descricao = "Workshop para iniciantes em Kotlin",
            dataHora = LocalDateTime.now().plusDays(10),
            localizacao = "ICMC - Sala 5-001",
            imagem = "https://exemplo.com/imagem.jpg",
            categoria = "Tecnologia",
            capacidadeMaxima = 30,
            organizador = organizador
        )
        
        participante1 = UsuarioParticipante(
            id = 2L,
            nome = "Participante 1",
            email = "participante1@usp.br",
            senha = "senha456"
        )
        
        participante2 = UsuarioParticipante(
            id = 3L,
            nome = "Participante 2",
            email = "participante2@usp.br",
            senha = "senha789"
        )
    }
    
    @Test
    fun `deve inicializar evento corretamente`() {
        assertEquals(1L, evento.id)
        assertEquals("Workshop de Kotlin", evento.titulo)
        assertEquals("Workshop para iniciantes em Kotlin", evento.descricao)
        assertEquals("ICMC - Sala 5-001", evento.localizacao)
        assertEquals("https://exemplo.com/imagem.jpg", evento.imagem)
        assertEquals("Tecnologia", evento.categoria)
        assertEquals(30, evento.capacidadeMaxima)
        assertEquals(organizador, evento.organizador)
        assertEquals(0, evento.numeroLikes)
        assertTrue(evento.participantesInteressados.isEmpty())
    }
    
    @Test
    fun `deve adicionar participante interessado com sucesso`() {
        assertTrue(evento.adicionarParticipanteInteressado(participante1))
        assertEquals(1, evento.participantesInteressados.size)
        assertTrue(evento.participantesInteressados.contains(participante1))
    }
    
    @Test
    fun `não deve adicionar participante interessado duplicado`() {
        evento.adicionarParticipanteInteressado(participante1)
        assertFalse(evento.adicionarParticipanteInteressado(participante1))
        assertEquals(1, evento.participantesInteressados.size)
    }
    
    @Test
    fun `deve remover participante interessado com sucesso`() {
        evento.adicionarParticipanteInteressado(participante1)
        assertTrue(evento.removerParticipanteInteressado(participante1))
        assertEquals(0, evento.participantesInteressados.size)
    }
    
    @Test
    fun `deve retornar falso ao tentar remover participante que não está na lista`() {
        assertFalse(evento.removerParticipanteInteressado(participante1))
    }
    
    @Test
    fun `deve adicionar like corretamente`() {
        assertEquals(0, evento.numeroLikes)
        evento.adicionarLike()
        assertEquals(1, evento.numeroLikes)
        evento.adicionarLike()
        assertEquals(2, evento.numeroLikes)
    }
    
    @Test
    fun `deve remover like corretamente`() {
        evento.adicionarLike()
        evento.adicionarLike()
        assertEquals(2, evento.numeroLikes)
        evento.removerLike()
        assertEquals(1, evento.numeroLikes)
        evento.removerLike()
        assertEquals(0, evento.numeroLikes)
    }
    
    @Test
    fun `não deve permitir número de likes negativo`() {
        assertEquals(0, evento.numeroLikes)
        evento.removerLike()
        assertEquals(0, evento.numeroLikes)
    }
    
    @Test
    fun `deve identificar evento que já ocorreu`() {
        val eventoPassado = Evento(
            id = 2L,
            titulo = "Evento Passado",
            descricao = "Este evento já ocorreu",
            dataHora = LocalDateTime.now().minusDays(1),
            localizacao = "ICMC",
            imagem = "url.jpg",
            categoria = "Palestra",
            capacidadeMaxima = 50,
            organizador = organizador
        )
        
        assertTrue(eventoPassado.jaOcorreu())
        assertFalse(evento.jaOcorreu())
    }
    
    @Test
    fun `deve identificar quando evento está lotado`() {
        val eventoComCapacidade2 = Evento(
            id = 3L,
            titulo = "Evento Pequeno",
            descricao = "Evento com capacidade para 2 pessoas",
            dataHora = LocalDateTime.now().plusDays(5),
            localizacao = "ICMC",
            imagem = "url.jpg",
            categoria = "Workshop",
            capacidadeMaxima = 2,
            organizador = organizador
        )
        
        assertFalse(eventoComCapacidade2.estaLotado())
        
        eventoComCapacidade2.adicionarParticipanteInteressado(participante1)
        assertFalse(eventoComCapacidade2.estaLotado())
        
        eventoComCapacidade2.adicionarParticipanteInteressado(participante2)
        assertTrue(eventoComCapacidade2.estaLotado())
    }
    
    @Test
    fun `deve comparar eventos corretamente pelo id`() {
        val eventoMesmoId = Evento(
            id = 1L,
            titulo = "Outro título",
            descricao = "Outra descrição",
            dataHora = LocalDateTime.now().plusDays(20),
            localizacao = "Outro local",
            imagem = "outra-imagem.jpg",
            categoria = "Outra categoria",
            capacidadeMaxima = 10,
            organizador = organizador
        )
        
        val eventoIdDiferente = Evento(
            id = 4L,
            titulo = "Workshop de Kotlin",
            descricao = "Workshop para iniciantes em Kotlin",
            dataHora = evento.dataHora,
            localizacao = evento.localizacao,
            imagem = evento.imagem,
            categoria = evento.categoria,
            capacidadeMaxima = evento.capacidadeMaxima,
            organizador = organizador
        )
        
        assertEquals(evento, eventoMesmoId)
        assertNotEquals(evento, eventoIdDiferente)
    }
}
