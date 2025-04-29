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
            imagemCapa = "https://exemplo.com/imagem-capa.jpg",
            categoria = "Tecnologia",
            organizador = organizador
        )
        
        imagemEvento = ImagemEvento(
            id = 1L,
            evento = evento,
            url = "https://exemplo.com/imagem-adicional.jpg",
            descricao = "Foto do palestrante",
            ordem = 1
        )
    }
    
    @Test
    fun `deve inicializar imagem evento corretamente`() {
        assertEquals(1L, imagemEvento.id)
        assertEquals(evento, imagemEvento.evento)
        assertEquals("https://exemplo.com/imagem-adicional.jpg", imagemEvento.url)
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
