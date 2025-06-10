package br.usp.eventUSP.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDateTime

class ImagemEventoTest {
    
    private lateinit var organizador: UsuarioOrganizador
    private lateinit var evento: Evento
    private lateinit var imagemEvento: ImagemEvento
    
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
        
        imagemEvento = ImagemEvento(
            id = 1L,
            evento = evento,
            url = "https://exemplo.com/imagem.jpg",
            descricao = "Foto do palestrante",
            ordem = 1
        )
        
        // Adicionamos a imagem à lista de imagens do evento
        evento.imagens.add(imagemEvento)
    }
    
    @Test
    fun `deve inicializar imagem evento corretamente`() {
        assertEquals(1L, imagemEvento.id)
        assertEquals(evento, imagemEvento.evento)
        assertEquals("https://exemplo.com/imagem.jpg", imagemEvento.url)
        assertEquals("Foto do palestrante", imagemEvento.descricao)
        assertEquals(1, imagemEvento.ordem)
    }
    
    @Test
    fun `deve inicializar imagem evento com valores padrão`() {
        val imagemSemDescricao = ImagemEvento(
            evento = evento,
            url = "https://exemplo.com/outra-imagem.jpg"
        )
        
        assertNull(imagemSemDescricao.id)
        assertEquals(evento, imagemSemDescricao.evento)
        assertEquals("https://exemplo.com/outra-imagem.jpg", imagemSemDescricao.url)
        assertNull(imagemSemDescricao.descricao)
        assertEquals(0, imagemSemDescricao.ordem)
    }
    
    @Test
    fun `deve adicionar imagem via método do evento`() {
        val novaImagem = evento.adicionarImagem(
            url = "https://exemplo.com/nova-imagem.jpg", 
            descricao = "Nova imagem", 
            ordem = 2
        )
        
        assertEquals(2, evento.imagens.size)
        assertTrue(evento.imagens.contains(novaImagem))
        assertEquals("https://exemplo.com/nova-imagem.jpg", novaImagem.url)
        assertEquals("Nova imagem", novaImagem.descricao)
        assertEquals(2, novaImagem.ordem)
    }
    
    @Test
    fun `deve remover imagem via método do evento`() {
        assertTrue(evento.removerImagem(imagemEvento))
        assertEquals(0, evento.imagens.size)
    }
    
    @Test
    fun `deve obter imagens ordenadas`() {
        // Adicionamos mais algumas imagens com ordens diferentes
        val imagem2 = evento.adicionarImagem(url = "https://exemplo.com/imagem2.jpg", ordem = 3)
        val imagem3 = evento.adicionarImagem(url = "https://exemplo.com/imagem3.jpg", ordem = 0)
        val imagem4 = evento.adicionarImagem(url = "https://exemplo.com/imagem4.jpg", ordem = 2)
        
        val imagensOrdenadas = evento.obterImagensOrdenadas()
        
        assertEquals(4, imagensOrdenadas.size)
        assertEquals(imagem3, imagensOrdenadas[0])  // ordem 0
        assertEquals(imagemEvento, imagensOrdenadas[1])  // ordem 1
        assertEquals(imagem4, imagensOrdenadas[2])  // ordem 2
        assertEquals(imagem2, imagensOrdenadas[3])  // ordem 3
    }
    
    @Test
    fun `deve comparar imagens evento corretamente pelo id`() {
        val imagemMesmoId = ImagemEvento(
            id = 1L,
            evento = evento,
            url = "https://exemplo.com/outra-url.jpg",
            descricao = "Outra descrição",
            ordem = 2
        )
        
        val imagemIdDiferente = ImagemEvento(
            id = 2L,
            evento = evento,
            url = imagemEvento.url,
            descricao = imagemEvento.descricao,
            ordem = imagemEvento.ordem
        )
        
        assertEquals(imagemEvento, imagemMesmoId)
        assertNotEquals(imagemEvento, imagemIdDiferente)
    }
    
    @Test
    fun `deve comparar imagens evento pela url quando id for nulo`() {
        val imagemSemId1 = ImagemEvento(
            evento = evento,
            url = "https://exemplo.com/imagem1.jpg"
        )
        
        val imagemSemId2 = ImagemEvento(
            evento = evento,
            url = "https://exemplo.com/imagem1.jpg"
        )
        
        val imagemSemId3 = ImagemEvento(
            evento = evento,
            url = "https://exemplo.com/imagem2.jpg"
        )
        
        assertEquals(imagemSemId1, imagemSemId2)
        assertNotEquals(imagemSemId1, imagemSemId3)
    }
}
