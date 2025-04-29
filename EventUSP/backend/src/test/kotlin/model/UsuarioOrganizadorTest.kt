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
        organizador = UsuarioOrganizador(
            id = 1L,
            nome = "Carlos Organizador",
            email = "carlos@usp.br",
            senha = "senha123",
            cpf = "12345678901",
            instituicao = "EESC",
            telefone = "16987654321"
        )
        
        // Criamos o evento, mas não o adicionamos à lista de eventos organizados ainda
        // pois queremos testar o método criarEvento()
        evento = Evento(
            id = 1L,
            titulo = "Feira de Ciências",
            descricao = "Feira anual de ciências da EESC",
            dataHora = LocalDateTime.now().plusDays(30),
            localizacao = "EESC - Área 1",
            imagem = "https://exemplo.com/feira.jpg",
            categoria = "Ciência",
            capacidadeMaxima = 200,
            organizador = organizador
        )
    }
    
    @Test
    fun `deve inicializar usuário organizador corretamente`() {
        assertEquals(1L, organizador.id)
        assertEquals("Carlos Organizador", organizador.nome)
        assertEquals("carlos@usp.br", organizador.email)
        assertEquals("senha123", organizador.senha)
        assertEquals("12345678901", organizador.cpf)
        assertEquals("EESC", organizador.instituicao)
        assertEquals("16987654321", organizador.telefone)
        assertTrue(organizador.eventosComLike.isEmpty())
        assertTrue(organizador.eventosInteressado.isEmpty())
        assertTrue(organizador.eventosOrganizados.isEmpty())
    }
    
    @Test
    fun `deve criar evento com sucesso`() {
        val novoEvento = organizador.criarEvento(
            titulo = "Palestra de Engenharia",
            descricao = "Palestra sobre inovações em engenharia",
            dataHora = LocalDateTime.now().plusDays(15),
            localizacao = "EESC - Auditório",
            imagem = "https://exemplo.com/palestra.jpg",
            categoria = "Engenharia",
            capacidadeMaxima = 80
        )
        
        assertEquals(1, organizador.eventosOrganizados.size)
        assertTrue(organizador.eventosOrganizados.contains(novoEvento))
        assertEquals("Palestra de Engenharia", novoEvento.titulo)
        assertEquals("Palestra sobre inovações em engenharia", novoEvento.descricao)
        assertEquals("EESC - Auditório", novoEvento.localizacao)
        assertEquals("https://exemplo.com/palestra.jpg", novoEvento.imagem)
        assertEquals("Engenharia", novoEvento.categoria)
        assertEquals(80, novoEvento.capacidadeMaxima)
        assertEquals(organizador, novoEvento.organizador)
    }
    
    @Test
    fun `deve cancelar evento com sucesso`() {
        // Adicionamos o evento à lista de eventos organizados manualmente para simular
        // que o evento já foi criado pelo organizador
        organizador.eventosOrganizados.add(evento)
        
        assertTrue(organizador.cancelarEvento(evento))
        assertEquals(0, organizador.eventosOrganizados.size)
    }
    
    @Test
    fun `não deve cancelar evento de outro organizador`() {
        val outroOrganizador = UsuarioOrganizador(
            id = 2L,
            nome = "Outro Organizador",
            email = "outro@usp.br",
            senha = "senha456",
            cpf = "98765432101"
        )
        
        val eventoDeOutroOrganizador = Evento(
            id = 2L,
            titulo = "Evento de Outro Organizador",
            descricao = "Descrição",
            dataHora = LocalDateTime.now().plusDays(10),
            localizacao = "Local",
            imagem = "imagem.jpg",
            categoria = "Categoria",
            capacidadeMaxima = 50,
            organizador = outroOrganizador
        )
        
        assertFalse(organizador.cancelarEvento(eventoDeOutroOrganizador))
    }
    
    @Test
    fun `deve atualizar evento com sucesso`() {
        // Adicionamos o evento à lista de eventos organizados manualmente
        organizador.eventosOrganizados.add(evento)
        
        val novoTitulo = "Feira de Ciências e Tecnologia"
        val novaDescricao = "Feira anual atualizada"
        val novaDataHora = LocalDateTime.now().plusDays(45)
        val novaLocalizacao = "EESC - Área 2"
        val novaImagem = "https://exemplo.com/feira-nova.jpg"
        val novaCategoria = "Ciência e Tecnologia"
        val novaCapacidade = 250
        
        assertTrue(organizador.atualizarEvento(
            evento = evento,
            titulo = novoTitulo,
            descricao = novaDescricao,
            dataHora = novaDataHora,
            localizacao = novaLocalizacao,
            imagem = novaImagem,
            categoria = novaCategoria,
            capacidadeMaxima = novaCapacidade
        ))
        
        assertEquals(novoTitulo, evento.titulo)
        assertEquals(novaDescricao, evento.descricao)
        assertEquals(novaDataHora, evento.dataHora)
        assertEquals(novaLocalizacao, evento.localizacao)
        assertEquals(novaImagem, evento.imagem)
        assertEquals(novaCategoria, evento.categoria)
        assertEquals(novaCapacidade, evento.capacidadeMaxima)
    }
    
    @Test
    fun `deve atualizar apenas campos não nulos`() {
        // Adicionamos o evento à lista de eventos organizados manualmente
        organizador.eventosOrganizados.add(evento)
        
        val tituloOriginal = evento.titulo
        val descricaoOriginal = evento.descricao
        val dataHoraOriginal = evento.dataHora
        val localizacaoOriginal = evento.localizacao
        val imagemOriginal = evento.imagem
        val categoriaOriginal = evento.categoria
        val capacidadeOriginal = evento.capacidadeMaxima
        
        // Atualizamos apenas o título e a capacidade
        assertTrue(organizador.atualizarEvento(
            evento = evento,
            titulo = "Novo Título",
            capacidadeMaxima = 300
        ))
        
        assertEquals("Novo Título", evento.titulo)
        assertEquals(descricaoOriginal, evento.descricao)
        assertEquals(dataHoraOriginal, evento.dataHora)
        assertEquals(localizacaoOriginal, evento.localizacao)
        assertEquals(imagemOriginal, evento.imagem)
        assertEquals(categoriaOriginal, evento.categoria)
        assertEquals(300, evento.capacidadeMaxima)
    }
    
    @Test
    fun `não deve atualizar evento de outro organizador`() {
        val outroOrganizador = UsuarioOrganizador(
            id = 2L,
            nome = "Outro Organizador",
            email = "outro@usp.br",
            senha = "senha456",
            cpf = "98765432101"
        )
        
        val eventoDeOutroOrganizador = Evento(
            id = 2L,
            titulo = "Evento de Outro Organizador",
            descricao = "Descrição",
            dataHora = LocalDateTime.now().plusDays(10),
            localizacao = "Local",
            imagem = "imagem.jpg",
            categoria = "Categoria",
            capacidadeMaxima = 50,
            organizador = outroOrganizador
        )
        
        assertFalse(organizador.atualizarEvento(
            evento = eventoDeOutroOrganizador,
            titulo = "Título Atualizado"
        ))
        
        // Verificamos que o título não foi alterado
        assertEquals("Evento de Outro Organizador", eventoDeOutroOrganizador.titulo)
    }
    
    @Test
    fun `deve herdar funcionalidades de UsuarioParticipante`() {
        val outroOrganizador = UsuarioOrganizador(
            id = 3L,
            nome = "Outro Organizador",
            email = "organizador2@usp.br",
            senha = "senha789",
            cpf = "45678912345"
        )
        
        val eventoDeOutroOrganizador = Evento(
            id = 3L,
            titulo = "Evento para Teste",
            descricao = "Descrição do evento",
            dataHora = LocalDateTime.now().plusDays(20),
            localizacao = "Local do evento",
            imagem = "imagem.jpg",
            categoria = "Categoria do evento",
            capacidadeMaxima = 100,
            organizador = outroOrganizador
        )
        
        // Testamos as funcionalidades herdadas de UsuarioParticipante
        assertTrue(organizador.darLike(eventoDeOutroOrganizador))
        assertTrue(organizador.demonstrarInteresse(eventoDeOutroOrganizador))
        
        assertEquals(1, organizador.eventosComLike.size)
        assertEquals(1, organizador.eventosInteressado.size)
        assertEquals(1, eventoDeOutroOrganizador.numeroLikes)
        assertEquals(1, eventoDeOutroOrganizador.participantesInteressados.size)
    }
    
    @Test
    fun `deve comparar organizadores corretamente`() {
        val organizadorMesmoId = UsuarioOrganizador(
            id = 1L,
            nome = "Nome Diferente",
            email = "email.diferente@usp.br",
            senha = "outrasenha",
            cpf = "98765432109"
        )
        
        val organizadorMesmoEmail = UsuarioOrganizador(
            id = 4L,
            nome = "Nome Diferente",
            email = "carlos@usp.br",
            senha = "outrasenha",
            cpf = "98765432109"
        )
        
        val organizadorMesmoCpf = UsuarioOrganizador(
            id = 5L,
            nome = "Nome Diferente",
            email = "email.diferente@usp.br",
            senha = "outrasenha",
            cpf = "12345678901"
        )
        
        val organizadorDiferente = UsuarioOrganizador(
            id = 6L,
            nome = "Nome Diferente",
            email = "email.diferente@usp.br",
            senha = "outrasenha",
            cpf = "98765432109"
        )
        
        assertEquals(organizador, organizadorMesmoId)
        assertEquals(organizador, organizadorMesmoEmail)
        assertEquals(organizador, organizadorMesmoCpf)  // Mesmo CPF
        assertNotEquals(organizador, organizadorDiferente)
    }
}
