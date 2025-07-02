package br.usp.eventUSP.database

import br.usp.eventUSP.model.Evento
import br.usp.eventUSP.model.ImagemEvento
import br.usp.eventUSP.model.UsuarioOrganizador
import br.usp.eventUSP.model.UsuarioParticipante
import br.usp.eventUSP.repository.EventoRepository
import br.usp.eventUSP.repository.ImagemEventoRepository
import br.usp.eventUSP.repository.UsuarioOrganizadorRepository
import br.usp.eventUSP.repository.UsuarioParticipanteRepository
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

/**
 * Objeto responsável por povoar a base de dados com dados iniciais para desenvolvimento.
 */
object DatabaseSeeder {

    fun init() {
        transaction {
            // Verifica se a base de dados já foi povoada (verificando se existe algum organizador)
            if (UsuarioOrganizadorRepository().findAll().isEmpty()) {
                println("Base de dados vazia. A povoar com dados iniciais...")

                // --- 1. Criar Utilizadores ---
                val organizadorRepo = UsuarioOrganizadorRepository()
                val participanteRepo = UsuarioParticipanteRepository()

                var organizador1 = UsuarioOrganizador()
                organizador1.nome = "Centro Cultural USP"
                organizador1.email = "ccusp@usp.br"
                organizador1.senha = "senha123"
                organizador1.fotoPerfil = "https://jc.usp.br/wp-content/uploads/2023/10/20231005_fachada_ccusp.jpg"
                organizador1 = organizadorRepo.create(organizador1)

                var organizador2 = UsuarioOrganizador()
                organizador2.nome = "IME Eventos"
                organizador2.email = "eventos@ime.usp.br"
                organizador2.senha = "senha123"
                organizador2.fotoPerfil = "https://www.ime.usp.br/templates/ime/images/logo-imew.png"
                organizador2 = organizadorRepo.create(organizador2)


                participanteRepo.create(
                    UsuarioParticipante(
                        nome = "Ana Silva",
                        email = "ana.silva@usp.br",
                        senha = "senha123"
                    )
                )

                participanteRepo.create(
                    UsuarioParticipante(
                        nome = "Bruno Costa",
                        email = "bruno.costa@usp.br",
                        senha = "senha123"
                    )
                )

                // --- 2. Criar Eventos ---
                val eventoRepo = EventoRepository()
                val imagemRepo = ImagemEventoRepository()

                val eventoShow = eventoRepo.create(
                    Evento(
                        titulo = "Show de Talentos CCUSP",
                        descricao = "Uma noite para celebrar os talentos musicais da nossa comunidade. Venha assistir e apoiar os artistas locais!",
                        dataHora = LocalDateTime.now().plusDays(10).withHour(19).withMinute(0),
                        localizacao = "Centro Cultural USP - R. do Anfiteatro, 109",
                        categoria = "Música",
                        organizador = organizador1
                    )
                )

                val eventoHackathon = eventoRepo.create(
                    Evento(
                        titulo = "Hackathon de IA do IME",
                        descricao = "24 horas de programação, inovação e colaboração. Forme a sua equipa e crie uma solução de IA para resolver um problema real.",
                        dataHora = LocalDateTime.now().plusDays(25).withHour(9).withMinute(0),
                        localizacao = "Auditório Jacy Monteiro - Bloco B do IME-USP",
                        categoria = "Tecnologia",
                        organizador = organizador2
                    )
                )

                // --- 3. Adicionar Imagens aos Eventos ---
                imagemRepo.create(
                    ImagemEvento(
                        eventoId = eventoShow.id!!,
                        url = "https://www.prceu.usp.br/wp-content/uploads/2023/04/Show-de-Talentos-do-CUASO-imagem-de-divulgacao-com-informacoes-do-evento.jpeg",
                        descricao = "Palco principal do evento",
                        ordem = 1
                    )
                )
                imagemRepo.create(
                    ImagemEvento(
                        eventoId = eventoHackathon.id!!,
                        url = "https://www.ime.usp.br/eventos/semanacomp/22/assets/images/logo.png",
                        descricao = "Equipas a trabalhar durante o evento",
                        ordem = 1
                    )
                )

                println("Povoamento da base de dados concluído.")
            } else {
                println("A base de dados já contém dados. Povoamento ignorado.")
            }
        }
    }
}
