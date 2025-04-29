package br.usp.eventUSP.model
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
            nome = "Organizador Teste",
            email = "organizador@usp.br",
            senha = "senha123",
            cpf = "12345678901",
            instituicao = "ICMC",
            telefone = "16912345678",
            fotoPerfil = "https://exemplo.com/foto-organizador.jpg"
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
        
        organizador.eventosOrganizados.add(evento)
    }
    
    @Test
    fun `deve inicializar organizador corretamente`() {
        assertEquals(1L, organizador.id)
        assertEquals("Organizador Teste", organizador.nome)
        assertEquals("organizador@usp.br", organizador.email)
        assertEquals("senha123", organizador.senha)
        assertEquals("12345678901", organizador.cpf)
        assertEquals("ICMC", organizador.instituicao)
        assertEquals("16912345678", organizador.telefone)
        assertEquals("https://exemplo.com/foto-organizador.jpg", organizador.fotoPerfil)
        assertEquals(1, organizador.eventosOrganizados.size)
        assertTrue(organizador.eventosComLike.isEmpty())
        assertTrue(organizador.eventosInteressado.isEmpty())
    }
    
    @Test
    fun `deve inicializar organizador com valores opcionais nulos`() {
        val organizadorSemOpcionais = UsuarioOrganizador(
            id = 2L,
            nome = "Outro Organizador",
            email = "outro@usp.br",
            senha = "senha456",
            cpf = "98765432109"
        )
        
        assertNull(organizadorSemOpcionais.instituicao)
        assertNull(organizadorSemOpcionais.telefone)
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
            imagemCapa = "https://exemplo.com/nova-capa.jpg",
            categoria = "Nova Categoria"
        )
        
        assertEquals("Novo Evento", novoEvento.titulo)
        assertEquals("Descrição do novo evento", novoEvento.descricao)
        assertEquals("Local do novo evento", novoEvento.localizacao)
        assertEquals("https://exemplo.com/nova-capa.jpg", novoEvento.imagemCapa)
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
        val outroOrganizador = UsuarioOrganizador(
            id = 3L,
            nome = "Outro Organizador",
            email = "outro@usp.br",
            senha = "senha789",
            cpf = "11122233344"
        )
        
        val eventoDeOutroOrganizador = Evento(
            id = 2L,
            titulo = "Evento de Outro Organizador",
            descricao = "Descrição",
            dataHora = LocalDateTime.now().plusDays(5),
            localizacao = "Local",
            imagemCapa = "https://exemplo.com/imagem.jpg",
            categoria = "Categoria",
            organizador = outroOrganizador
        )
        
        assertFalse(organizador.cancelarEvento(eventoDeOutroOrganizador))
    }
    
    @Test
    fun `deve atualizar evento com sucesso`() {
        assertTrue(organizador.atualizarEvento(
            evento = evento,
            titulo = "Título Atualizado",
            descricao = "Descrição Atualizada",
            dataHora = LocalDateTime.now().plusDays(20),
            localizacao = "Local Atualizado",
            imagemCapa = "https://exemplo.com/capa-atualizada.jpg",
            categoria = "Categoria Atualizada"
        ))
        
        assertEquals("Título Atualizado", evento.titulo)
        assertEquals("Descrição Atualizada", evento.descricao)
        assertEquals("Local Atualizado", evento.localizacao)
        assertEquals("https://exemplo.com/capa-atualizada.jpg", evento.imagemCapa)
        assertEquals("Categoria Atualizada", evento.categoria)
    }
    
    @Test
    fun `deve atualizar apenas campos não nulos do evento`() {
        val dataHoraOriginal = evento.dataHora
        val localizacaoOriginal = evento.localizacao
        val imagemCapaOriginal = evento.imagemCapa
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
        assertEquals(imagemCapaOriginal, evento.imagemCapa)
        assertEquals(categoriaOriginal, evento.categoria)
    }
    
    @Test
    fun `não deve atualizar evento de outro organizador`() {
        val outroOrganizador = UsuarioOrganizador(
            id = 3L,
            nome = "Outro Organizador",
            email = "outro@usp.br",
            senha = "senha789",
            cpf = "11122233344"
        )
        
        val eventoDeOutroOrganizador = Evento(
            id = 2L,
            titulo = "Evento de Outro Organizador",
            descricao = "Descrição",
            dataHora = LocalDateTime.now().plusDays(5),
            localizacao = "Local",
            imagemCapa = "https://exemplo.com/imagem.jpg",
            categoria = "Categoria",
            organizador = outroOrganizador
        )
        
        assertFalse(organizador.atualizarEvento(
            evento = eventoDeOutroOrganizador,
            titulo = "Tentativa de Atualização"
        ))
        
        assertEquals("Evento de Outro Organizador", eventoDeOutroOrganizador.titulo)
    }
    
    @Test
    fun `deve adicionar imagem adicional ao evento com sucesso`() {
        val imagem = organizador.adicionarImagemAoEvento(
            evento = evento,
            url = "https://exemplo.com/imagem-adicional.jpg",
            descricao = "Foto do local",
            ordem = 1
        )
        
        assertNotNull(imagem)
        assertEquals(1, evento.imagensAdicionais.size)
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
        
        assertEquals(3, evento.imagensAdicionais.size)
        assertNotNull(imagem1)
        assertNotNull(imagem2)
        assertNotNull(imagem3)
    }
    
    @Test
    fun `não deve adicionar imagem a evento de outro organizador`() {
        val outroOrganizador = UsuarioOrganizador(
            id = 3L,
            nome = "Outro Organizador",
            email = "outro@usp.br",
            senha = "senha789",
            cpf = "11122233344"
        )
        
        val eventoDeOutroOrganizador = Evento(
            id = 2L,
            titulo = "Evento de Outro Organizador",
            descricao = "Descrição",
            dataHora = LocalDateTime.now().plusDays(5),
            localizacao = "Local",
            imagemCapa = "https://exemplo.com/imagem.jpg",
            categoria = "Categoria",
            organizador = outroOrganizador
        )
        
        val imagem = organizador.adicionarImagemAoEvento(
            evento = eventoDeOutroOrganizador,
            url = "https://exemplo.com/imagem-nao-permitida.jpg"
        )
        
        assertNull(imagem)
        assertEquals(0, eventoDeOutroOrganizador.imagensAdicionais.size)
    }
    
    @Test
    fun `deve remover imagem de evento com sucesso`() {
        val imagem = organizador.adicionarImagemAoEvento(
            evento,
            "https://exemplo.com/imagem-para-remover.jpg"
        )
        
        assertNotNull(imagem)
        assertEquals(1, evento.imagensAdicionais.size)
        
        assertTrue(organizador.removerImagemDoEvento(evento, imagem!!))
        assertEquals(0, evento.imagensAdicionais.size)
    }
    
    @Test
    fun `não deve remover imagem de evento de outro organizador`() {
        val outroOrganizador = UsuarioOrganizador(
            id = 3L,
            nome = "Outro Organizador",
            email = "outro@usp.br",
            senha = "senha789",
            cpf = "11122233344"
        )
        
        val eventoDeOutroOrganizador = Evento(
            id = 2L,
            titulo = "Evento de Outro Organizador",
            descricao = "Descrição",
            dataHora = LocalDateTime.now().plusDays(5),
            localizacao = "Local",
            imagemCapa = "https://exemplo.com/imagem.jpg",
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
        val organizadorMesmoIdDiferenteCpf = UsuarioOrganizador(
            id = 1L,
            nome = "Outro Nome",
            email = "outro@usp.br",
            senha = "outrasenha",
            cpf = "99988877766"
        )
        
        val organizadorMesmoCpf = UsuarioOrganizador(
            id = 99L,
            nome = "Outro Nome",
            email = "outro@usp.br",
            senha = "outrasenha",
            cpf = "12345678901"
        )
        
        val organizadorMesmoEmail = UsuarioOrganizador(
            id = 100L,
            nome = "Outro Nome",
            email = "organizador@usp.br",
            senha = "outrasenha",
            cpf = "99988877766"
        )
        
        assertEquals(organizador, organizadorMesmoIdDiferenteCpf)
        assertEquals(organizador, organizadorMesmoCpf)
        assertEquals(organizador, organizadorMesmoEmail)
    }
}
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
            categoria = "Engenharia"
        )
        
        assertEquals(1, organizador.eventosOrganizados.size)
        assertTrue(organizador.eventosOrganizados.contains(novoEvento))
        assertEquals("Palestra de Engenharia", novoEvento.titulo)
        assertEquals("Palestra sobre inovações em engenharia", novoEvento.descricao)
        assertEquals("EESC - Auditório", novoEvento.localizacao)
        assertEquals("https://exemplo.com/palestra.jpg", novoEvento.imagem)
        assertEquals("Engenharia", novoEvento.categoria)
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
        
        assertTrue(organizador.atualizarEvento(
            evento = evento,
            titulo = novoTitulo,
            descricao = novaDescricao,
            dataHora = novaDataHora,
            localizacao = novaLocalizacao,
            imagem = novaImagem,
            categoria = novaCategoria
        ))
        
        assertEquals(novoTitulo, evento.titulo)
        assertEquals(novaDescricao, evento.descricao)
        assertEquals(novaDataHora, evento.dataHora)
        assertEquals(novaLocalizacao, evento.localizacao)
        assertEquals(novaImagem, evento.imagem)
        assertEquals(novaCategoria, evento.categoria)
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
        
        // Atualizamos apenas o título
        assertTrue(organizador.atualizarEvento(
            evento = evento,
            titulo = "Novo Título"
        ))
        
        assertEquals("Novo Título", evento.titulo)
        assertEquals(descricaoOriginal, evento.descricao)
        assertEquals(dataHoraOriginal, evento.dataHora)
        assertEquals(localizacaoOriginal, evento.localizacao)
        assertEquals(imagemOriginal, evento.imagem)
        assertEquals(categoriaOriginal, evento.categoria)
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
