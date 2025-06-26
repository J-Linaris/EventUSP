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
        organizador = UsuarioOrganizador()
        organizador.id = 1L
        organizador.nome = "Organizador Teste"
        organizador.email = "organizador@usp.br"
        organizador.senha = "senha123"
        organizador.fotoPerfil = "https://exemplo.com/foto-organizador.jpg"
        
        evento = Evento(
            id = 1L,
            titulo = "Workshop de Kotlin",
            descricao = "Workshop para iniciantes em Kotlin",
            dataHora = LocalDateTime.now().plusDays(10),
            localizacao = "ICMC - Sala 5-001",
            categoria = "Tecnologia",
            organizador = organizador
        )
        
        participante1 = UsuarioParticipante(
            id = 2L,
            nome = "Participante 1",
            email = "participante1@usp.br",
            senha = "senha456",
            fotoPerfil = "https://exemplo.com/foto-participante1.jpg"
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
        assertEquals("Tecnologia", evento.categoria)
        assertEquals(organizador, evento.organizador)
        assertEquals(0, evento.numeroLikes)
        assertTrue(evento.participantesInteressados.isEmpty())
        assertTrue(evento.imagens.isEmpty())
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
            categoria = "Palestra",
            organizador = organizador
        )
        
        assertTrue(eventoPassado.jaOcorreu())
        assertFalse(evento.jaOcorreu())
    }
    
    @Test
    fun `deve adicionar imagem ao evento com sucesso`() {
        val imagem = evento.adicionarImagem(
            url = "https://exemplo.com/imagem-adicional.jpg",
            descricao = "Descrição da imagem"
        )
        
        assertEquals(1, evento.imagens.size)
     //   assertEquals(evento, imagem.evento)
        assertEquals("https://exemplo.com/imagem-adicional.jpg", imagem.url)
        assertEquals("Descrição da imagem", imagem.descricao)
        assertEquals(0, imagem.ordem) // Primeira imagem recebe ordem 0
    }
    
    @Test
    fun `deve adicionar múltiplas imagens com ordem correta`() {
        val imagem1 = evento.adicionarImagem("https://exemplo.com/imagem1.jpg")
        val imagem2 = evento.adicionarImagem("https://exemplo.com/imagem2.jpg")
        val imagem3 = evento.adicionarImagem("https://exemplo.com/imagem3.jpg")
        
        assertEquals(3, evento.imagens.size)
        assertEquals(0, imagem1.ordem)
        assertEquals(1, imagem2.ordem)
        assertEquals(2, imagem3.ordem)
    }
    
    @Test
    fun `deve adicionar imagem com ordem personalizada`() {
        val imagem = evento.adicionarImagem(
            url = "https://exemplo.com/imagem-especial.jpg",
            ordem = 5
        )
        
        assertEquals(5, imagem.ordem)
    }
    
    @Test
    fun `deve remover imagem do evento com sucesso`() {
        val imagem = evento.adicionarImagem("https://exemplo.com/imagem-para-remover.jpg")
        
        assertEquals(1, evento.imagens.size)
        assertTrue(evento.removerImagem(imagem))
        assertEquals(0, evento.imagens.size)
    }
    
    @Test
    fun `deve retornar falso ao tentar remover imagem que não existe no evento`() {
        val outroEvento = Evento(
            id = 99L,
            titulo = "Outro Evento",
            descricao = "Descrição",
            dataHora = LocalDateTime.now().plusDays(5),
            localizacao = "Local",
            categoria = "Categoria",
            organizador = organizador
        )
        
        val imagemDeOutroEvento = ImagemEvento(
        //    evento = outroEvento,
            url = "https://exemplo.com/imagem-outro-evento.jpg"
        )
        
        assertFalse(evento.removerImagem(imagemDeOutroEvento))
    }
    
    @Test
    fun `deve obter imagens ordenadas corretamente`() {
        // Adiciona imagens fora de ordem
        val imagem2 = evento.adicionarImagem("https://exemplo.com/imagem2.jpg", ordem = 2)
        val imagem0 = evento.adicionarImagem("https://exemplo.com/imagem0.jpg", ordem = 0)
        val imagem1 = evento.adicionarImagem("https://exemplo.com/imagem1.jpg", ordem = 1)
        
        val imagensOrdenadas = evento.obterImagensOrdenadas()
        
        assertEquals(3, imagensOrdenadas.size)
        assertEquals(imagem0, imagensOrdenadas[0])
        assertEquals(imagem1, imagensOrdenadas[1])
        assertEquals(imagem2, imagensOrdenadas[2])
    }
    
    @Test
    fun `deve comparar eventos corretamente pelo id`() {
        val eventoMesmoId = Evento(
            id = 1L,
            titulo = "Outro título",
            descricao = "Outra descrição",
            dataHora = LocalDateTime.now().plusDays(20),
            localizacao = "Outro local",
            categoria = "Outra categoria",
            organizador = organizador
        )
        
        val eventoIdDiferente = Evento(
            id = 4L,
            titulo = "Workshop de Kotlin",
            descricao = "Workshop para iniciantes em Kotlin",
            dataHora = evento.dataHora,
            localizacao = evento.localizacao,
            categoria = evento.categoria,
            organizador = organizador
        )
        
        assertEquals(evento, eventoMesmoId)
        assertNotEquals(evento, eventoIdDiferente)
    }
}
