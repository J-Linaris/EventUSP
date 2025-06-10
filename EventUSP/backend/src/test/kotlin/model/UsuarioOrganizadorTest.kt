package br.usp.eventUSP.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDateTime

class UsuarioOrganizadorTest {
    
    private lateinit var organizador: UsuarioOrganizador
    private lateinit var evento: Evento
    
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
        
        // Adiciona uma imagem ao evento
        evento.adicionarImagem("https://exemplo.com/imagem-capa.jpg")
        
        organizador.eventosOrganizados.add(evento)
    }
    
    @Test
    fun `deve inicializar organizador corretamente`() {
        assertEquals(1L, organizador.id)
        assertEquals("Organizador Teste", organizador.nome)
        assertEquals("organizador@usp.br", organizador.email)
        assertEquals("senha123", organizador.senha)
        assertEquals("https://exemplo.com/foto-organizador.jpg", organizador.fotoPerfil)
        assertEquals(1, organizador.eventosOrganizados.size)
        assertTrue(organizador.eventosComLike.isEmpty())
        assertTrue(organizador.eventosInteressado.isEmpty())
    }
    
    @Test
    fun `deve inicializar organizador com valores opcionais nulos`() {
//        val organizadorSemOpcionais = UsuarioOrganizador(
//            id = 2L,
//            nome = "Outro Organizador",
//            email = "outro@usp.br",
//            senha = "senha456"
//        )
        val organizadorSemOpcionais = UsuarioOrganizador()
        organizadorSemOpcionais.id = 2L
        organizadorSemOpcionais.nome = "Outro Organizador"
        organizadorSemOpcionais.email = "outro@usp.br"
        organizadorSemOpcionais.senha = "senha456"
//        organizador.fotoPerfil = "https://exemplo.com/foto-organizador-outro.jpg"
        assertNull(organizadorSemOpcionais.fotoPerfil)
    }
    
    @Test
    fun `deve atualizar foto de perfil com sucesso`() {
        organizador.atualizarFotoPerfil("https://exemplo.com/nova-foto.jpg")
        assertEquals("https://exemplo.com/nova-foto.jpg", organizador.fotoPerfil)
    }
    
    @Test
    fun `deve criar evento com sucesso`() {
        val novoEvento = organizador.criarEvento(
            titulo = "Novo Evento",
            descricao = "Descrição do novo evento",
            dataHora = LocalDateTime.now().plusDays(15),
            localizacao = "Local do novo evento",
            categoria = "Nova Categoria"
        )
        
        // Adiciona uma imagem ao evento
        novoEvento.adicionarImagem("https://exemplo.com/nova-capa.jpg")
        
        assertEquals("Novo Evento", novoEvento.titulo)
        assertEquals("Descrição do novo evento", novoEvento.descricao)
        assertEquals("Local do novo evento", novoEvento.localizacao)
        assertEquals(1, novoEvento.imagens.size)
        assertEquals("https://exemplo.com/nova-capa.jpg", novoEvento.imagens[0].url)
        assertEquals("Nova Categoria", novoEvento.categoria)
        assertEquals(organizador, novoEvento.organizador)
        assertTrue(organizador.eventosOrganizados.contains(novoEvento))
    }
    
    @Test
    fun `deve cancelar evento com sucesso`() {
        assertTrue(organizador.cancelarEvento(evento))
        assertEquals(0, organizador.eventosOrganizados.size)
    }
    
    @Test
    fun `não deve cancelar evento de outro organizador`() {
//        val outroOrganizador = UsuarioOrganizador(
//            id = 3L,
//            nome = "Outro Organizador",
//            email = "outro@usp.br",
//            senha = "senha789"
//        )
        val outroOrganizador = UsuarioOrganizador()
        outroOrganizador.id = 3L
        outroOrganizador.nome = "Outro Organizador"
        outroOrganizador.email = "outro@usp.br"
        outroOrganizador.senha = "senha789"


        val eventoDeOutroOrganizador = Evento(
            id = 2L,
            titulo = "Evento de Outro Organizador",
            descricao = "Descrição",
            dataHora = LocalDateTime.now().plusDays(5),
            localizacao = "Local",
            categoria = "Categoria",
            organizador = outroOrganizador
        )
        
        // Adiciona uma imagem ao evento
        eventoDeOutroOrganizador.adicionarImagem("https://exemplo.com/imagem.jpg")
        
        assertFalse(organizador.cancelarEvento(eventoDeOutroOrganizador))
    }
    
    @Test
    fun `deve atualizar evento com sucesso`() {
        val urlImagemNova = "https://exemplo.com/capa-atualizada.jpg"
        
        assertTrue(organizador.atualizarEvento(
            evento = evento,
            titulo = "Título Atualizado",
            descricao = "Descrição Atualizada",
            dataHora = LocalDateTime.now().plusDays(20),
            localizacao = "Local Atualizado",
            categoria = "Categoria Atualizada"
        ))
        
        // Atualiza a imagem do evento
        evento.imagens.clear()
        evento.adicionarImagem(urlImagemNova)
        
        assertEquals("Título Atualizado", evento.titulo)
        assertEquals("Descrição Atualizada", evento.descricao)
        assertEquals("Local Atualizado", evento.localizacao)
        assertEquals(1, evento.imagens.size)
        assertEquals(urlImagemNova, evento.imagens[0].url)
        assertEquals("Categoria Atualizada", evento.categoria)
    }
    
    @Test
    fun `deve atualizar apenas campos não nulos do evento`() {
        val dataHoraOriginal = evento.dataHora
        val localizacaoOriginal = evento.localizacao
        val imagensOriginais = ArrayList(evento.imagens)
        val categoriaOriginal = evento.categoria
        
        assertTrue(organizador.atualizarEvento(
            evento = evento,
            titulo = "Apenas Título Atualizado",
            descricao = "Apenas Descrição Atualizada"
        ))
        
        assertEquals("Apenas Título Atualizado", evento.titulo)
        assertEquals("Apenas Descrição Atualizada", evento.descricao)
        assertEquals(dataHoraOriginal, evento.dataHora)
        assertEquals(localizacaoOriginal, evento.localizacao)
        assertEquals(imagensOriginais.size, evento.imagens.size)
        assertEquals(imagensOriginais[0].url, evento.imagens[0].url)
        assertEquals(categoriaOriginal, evento.categoria)
    }
    
    @Test
    fun `não deve atualizar evento de outro organizador`() {
//        val outroOrganizador = UsuarioOrganizador(
//            id = 3L,
//            nome = "Outro Organizador",
//            email = "outro@usp.br",
//            senha = "senha789"
//        )
        val outroOrganizador = UsuarioOrganizador()
        outroOrganizador.id = 3L
        outroOrganizador.nome = "Outro Organizador"
        outroOrganizador.email = "outro@usp.br"
        outroOrganizador.senha = "senha789"

        val eventoDeOutroOrganizador = Evento(
            id = 2L,
            titulo = "Evento de Outro Organizador",
            descricao = "Descrição",
            dataHora = LocalDateTime.now().plusDays(5),
            localizacao = "Local",
            categoria = "Categoria",
            organizador = outroOrganizador
        )
        
        // Adiciona uma imagem ao evento
        eventoDeOutroOrganizador.adicionarImagem("https://exemplo.com/imagem.jpg")
        
        assertFalse(organizador.atualizarEvento(
            evento = eventoDeOutroOrganizador,
            titulo = "Tentativa de Atualização"
        ))
        
        assertEquals("Evento de Outro Organizador", eventoDeOutroOrganizador.titulo)
    }
    
    @Test
    fun `deve adicionar imagem ao evento com sucesso`() {
        val imagem = organizador.adicionarImagemAoEvento(
            evento = evento,
            url = "https://exemplo.com/imagem-adicional.jpg",
            descricao = "Foto do local",
            ordem = 1
        )
        
        assertNotNull(imagem)
        assertEquals(2, evento.imagens.size)  // Já tinha uma imagem do setup
        assertEquals("https://exemplo.com/imagem-adicional.jpg", imagem?.url)
        assertEquals("Foto do local", imagem?.descricao)
        assertEquals(1, imagem?.ordem)
        assertEquals(evento, imagem?.evento)
    }
    
    @Test
    fun `deve adicionar múltiplas imagens ao evento com sucesso`() {
        val imagem1 = organizador.adicionarImagemAoEvento(evento, "https://exemplo.com/imagem1.jpg")
        val imagem2 = organizador.adicionarImagemAoEvento(evento, "https://exemplo.com/imagem2.jpg")
        val imagem3 = organizador.adicionarImagemAoEvento(evento, "https://exemplo.com/imagem3.jpg")
        
        assertEquals(4, evento.imagens.size)  // Já tinha uma imagem do setup
        assertNotNull(imagem1)
        assertNotNull(imagem2)
        assertNotNull(imagem3)
    }
    
    @Test
    fun `não deve adicionar imagem a evento de outro organizador`() {
//        val outroOrganizador = UsuarioOrganizador(
//            id = 3L,
//            nome = "Outro Organizador",
//            email = "outro@usp.br",
//            senha = "senha789"
//        )
        val outroOrganizador = UsuarioOrganizador()
        outroOrganizador.id = 3L
        outroOrganizador.nome = "Outro Organizador"
        outroOrganizador.email = "outro@usp.br"
        outroOrganizador.senha = "senha789"

        val eventoDeOutroOrganizador = Evento(
            id = 2L,
            titulo = "Evento de Outro Organizador",
            descricao = "Descrição",
            dataHora = LocalDateTime.now().plusDays(5),
            localizacao = "Local",
            categoria = "Categoria",
            organizador = outroOrganizador
        )
        
        val imagem = organizador.adicionarImagemAoEvento(
            evento = eventoDeOutroOrganizador,
            url = "https://exemplo.com/imagem-nao-permitida.jpg"
        )
        
        assertNull(imagem)
        assertEquals(0, eventoDeOutroOrganizador.imagens.size)
    }
    
    @Test
    fun `deve remover imagem de evento com sucesso`() {
        val imagem = organizador.adicionarImagemAoEvento(
            evento,
            "https://exemplo.com/imagem-para-remover.jpg"
        )
        
        assertNotNull(imagem)
        assertEquals(2, evento.imagens.size)  // Já tinha uma imagem do setup
        
        assertTrue(organizador.removerImagemDoEvento(evento, imagem!!))
        assertEquals(1, evento.imagens.size)  // Voltou a ter só a imagem do setup
    }
    
    @Test
    fun `não deve remover imagem de evento de outro organizador`() {
//        val outroOrganizador = UsuarioOrganizador(
//            id = 3L,
//            nome = "Outro Organizador",
//            email = "outro@usp.br",
//            senha = "senha789"
//        )
        val outroOrganizador = UsuarioOrganizador()
        outroOrganizador.id = 3L
        outroOrganizador.nome = "Outro Organizador"
        outroOrganizador.email = "outro@usp.br"
        outroOrganizador.senha = "senha789"
        
        val eventoDeOutroOrganizador = Evento(
            id = 2L,
            titulo = "Evento de Outro Organizador",
            descricao = "Descrição",
            dataHora = LocalDateTime.now().plusDays(5),
            localizacao = "Local",
            categoria = "Categoria",
            organizador = outroOrganizador
        )
        
        val imagem = ImagemEvento(
            evento = eventoDeOutroOrganizador,
            url = "https://exemplo.com/imagem-outro-evento.jpg"
        )
        
        assertFalse(organizador.removerImagemDoEvento(eventoDeOutroOrganizador, imagem))
    }
    
    @Test
    fun `deve comparar organizadores corretamente`() {
//        val organizadorMesmoId = UsuarioOrganizador(
//            id = 1L,
//            nome = "Outro Nome",
//            email = "outro@usp.br",
//            senha = "outrasenha"
//        )
        val organizadorMesmoId = UsuarioOrganizador()
        organizadorMesmoId.id = 1L
        organizadorMesmoId.nome = "Outro Nome"
        organizadorMesmoId.email = "outro@usp.br"
        organizadorMesmoId.senha = "outrasenha"
        
//        val organizadorMesmoEmail = UsuarioOrganizador(
//            id = 100L,
//            nome = "Outro Nome",
//            email = "organizador@usp.br",
//            senha = "outrasenha"
//        )
        val organizadorMesmoEmail = UsuarioOrganizador()
        organizadorMesmoEmail.id = 100L
        organizadorMesmoEmail.nome = "Outro Nome"
        organizadorMesmoEmail.email = "organizador@usp.br"
        organizadorMesmoEmail.senha = "outrasenha"

        assertEquals(organizador, organizadorMesmoId)
        assertEquals(organizador, organizadorMesmoEmail)
    }
}
