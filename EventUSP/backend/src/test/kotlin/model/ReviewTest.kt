package br.usp.eventUSP.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class ReviewTest {
    
    private lateinit var organizador: UsuarioOrganizador
    private lateinit var participante: UsuarioParticipante
    private lateinit var evento: Evento
    private lateinit var review: Review
    
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
            nome = "Participante Teste",
            email = "participante@usp.br",
            senha = "senha456",
            fotoPerfil = "https://exemplo.com/foto-participante.jpg"
        )
        
        evento = Evento(
            id = 1L,
            titulo = "Evento Teste",
            descricao = "Descrição do evento",
            dataHora = LocalDateTime.now().minusDays(3), // Evento ocorreu há 3 dias
            localizacao = "Local do Evento",
            categoria = "Categoria",
            organizador = organizador
        )
        
        // Adiciona o participante como interessado no evento
        evento.adicionarParticipanteInteressado(participante)
        
        review = Review(
            id = 1L,
            eventoId = evento.id!!,
            participanteId = participante.id!!,
            nota = 4,
            comentario = "Evento muito bom, recomendo!"
        )
    }
    
    @Test
    fun `deve inicializar review corretamente`() {
        assertEquals(1L, review.id)
        assertEquals(evento.id, review.eventoId)
        assertEquals(participante.id, review.participanteId)
        assertEquals(4, review.nota)
        assertEquals("Evento muito bom, recomendo!", review.comentario)
        assertNotNull(review.dataHora)
    }
    
    @Test
    fun `deve lançar exceção para nota fora do intervalo válido`() {
        assertThrows<IllegalArgumentException> {
            Review(
                eventoId = evento.id!!,
                participanteId = participante.id!!,
                nota = 6, // Nota inválida, maior que 5
                comentario = "Comentário"
            )
        }
        
        assertThrows<IllegalArgumentException> {
            Review(
                eventoId = evento.id!!,
                participanteId = participante.id!!,
                nota = -1, // Nota inválida, menor que 0
                comentario = "Comentário"
            )
        }
    }
    
    @Test
    fun `deve aceitar todas as notas no intervalo válido`() {
        for (nota in 0..5) {
            val r = Review(
                eventoId = evento.id!!,
                participanteId = participante.id!!,
                nota = nota,
                comentario = "Comentário"
            )
            assertEquals(nota, r.nota)
        }
    }
    
    @Test
    fun `deve comparar reviews corretamente`() {
        val reviewMesmoId = Review(
            id = 1L,
            eventoId = evento.id!!,
            participanteId = participante.id!!,
            nota = 5, // Nota diferente
            comentario = "Outro comentário" // Comentário diferente
        )
        
        val reviewMesmoEventoParticipante = Review(
            eventoId = evento.id!!,
            participanteId = participante.id!!,
            nota = 3,
            comentario = "Outro comentário"
        )
        
        val outroParticipante = UsuarioParticipante(
            id = 3L,
            nome = "Outro Participante",
            email = "outro@usp.br",
            senha = "senha789"
        )
        
        val reviewOutroParticipante = Review(
            eventoId = evento.id!!,
            participanteId = outroParticipante.id!!,
            nota = 4,
            comentario = "Comentário"
        )
        
        assertEquals(review, reviewMesmoId)
        assertEquals(review, reviewMesmoEventoParticipante)
        assertNotEquals(review, reviewOutroParticipante)
    }
}
