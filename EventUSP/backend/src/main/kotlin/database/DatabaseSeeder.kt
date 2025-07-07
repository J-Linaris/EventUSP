package br.usp.eventUSP.database


// 1. IMPORTE OS OBJETOS CORRETOS DO SEU ARQUIVO Tables.kt
import br.usp.eventUSP.database.tables.EventoTable
import br.usp.eventUSP.database.tables.ImagemEventoTable
import br.usp.eventUSP.database.tables.ParticipantesInteressadosTable
import br.usp.eventUSP.database.tables.ReviewTable
import br.usp.eventUSP.database.tables.UsuarioOrganizadorTable
import br.usp.eventUSP.database.tables.UsuarioParticipanteTable


import br.usp.eventUSP.model.Evento
import br.usp.eventUSP.model.ImagemEvento
import br.usp.eventUSP.model.UsuarioOrganizador
import br.usp.eventUSP.model.UsuarioParticipante
import br.usp.eventUSP.repository.EventoRepository
import br.usp.eventUSP.repository.ImagemEventoRepository
import br.usp.eventUSP.repository.UsuarioOrganizadorRepository
import br.usp.eventUSP.repository.UsuarioParticipanteRepository
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.deleteAll
import java.time.LocalDateTime

/**
 * Objeto responsável por povoar a base de dados com dados iniciais para desenvolvimento.
 */
object DatabaseSeeder {

    fun init() {
        transaction {
            // Verifica se a base de dados já foi povoada (verificando se existe algum organizador)
//            if (UsuarioOrganizadorRepository().findAll().isEmpty()) {
//                println("Base de dados vazia. A povoar com dados iniciais...")
            // --- 1. LIMPAR DADOS EXISTENTES ---
            println("Limpando a base de dados antiga...")

            // 2. CHAME deleteAll() NOS OBJETOS DE TABELA CORRETOS E NA ORDEM CERTA
            // Tabelas de junção e dependentes primeiro:
            ParticipantesInteressadosTable.deleteAll()
            ImagemEventoTable.deleteAll()
            ReviewTable.deleteAll()

            // Tabelas principais depois:
            EventoTable.deleteAll()
            UsuarioParticipanteTable.deleteAll()
            UsuarioOrganizadorTable.deleteAll()

            println("Limpeza concluída. Repovoando a base de dados...")
            println("Limpeza concluída. Repovoando a base de dados...")

                // --- 1. Criar Utilizadores ---
                val organizadorRepo = UsuarioOrganizadorRepository()
                val participanteRepo = UsuarioParticipanteRepository()

                var organizador1 = UsuarioOrganizador()
                organizador1.nome = "Centro Cultural USP"
                organizador1.email = "ccusp@usp.br"
                organizador1.senha = "senha123"
                organizador1.fotoPerfil = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSABqV-VQBSF1Qyj1Qyo6RiLfpA0McI1leTvQ&s"
                organizador1 = organizadorRepo.create(organizador1)

                var organizador2 = UsuarioOrganizador()
                organizador2.nome = "IME Eventos"
                organizador2.email = "eventos@ime.usp.br"
                organizador2.senha = "senha123"
                organizador2.fotoPerfil = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSABqV-VQBSF1Qyj1Qyo6RiLfpA0McI1leTvQ&s"
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
                        organizador = organizador1,
                        numeroLikes = 0
                    )
                )

                val eventoFut = eventoRepo.create(
                    Evento(
                        titulo = "Apresentação do Messi no FutCampo IME-USP",
                        descricao = "Depois de muito esforço, ele veio!! Venham conhecer a lenda!",
                        dataHora = LocalDateTime.now().plusDays(2).withHour(19).withMinute(0),
                        localizacao = "Praça do Relógio Solar, Tv. do Jardim - Butantã, São Paulo - SP, 05508-000",
                        categoria = "Esportivo",
                        organizador = organizador1,
                        numeroLikes = 0
                    )
                )

            val eventoFesta = eventoRepo.create(
                Evento(
                    titulo = "FAU Junina",
                    descricao = "Descrição gigante!",
                    dataHora = LocalDateTime.now().plusDays(2).withHour(19).withMinute(0),
                    localizacao = "Universidade de São Paulo - R. do Lago, 876 - Butantã, São Paulo - SP, 05508-080",
                    categoria = "Cultural",
                    organizador = organizador1,
                    numeroLikes = 0
                )
            )

                val eventoNatacao = eventoRepo.create(
                    Evento(
                        titulo = "Copa USP de Natação",
                        descricao = "VENHAM APOIAR SEUS ATLETAS!!\nAtléticas Participantes:\n" +
                                "- IME\n - POLI\n- FEA\n- SANFRAN\n- ECA\n- FAU\n- EACH\n- IRI",
                        dataHora = LocalDateTime.now().plusDays(5).withHour(19).withMinute(0),
                        localizacao = "Av. Ibirapuera, 1315 - Vila Clementino, São Paulo - SP, 04029-000",
                        categoria = "Esportivo",
                        organizador = organizador1,
                        numeroLikes = 0
                    )
                )

                val eventoVoltaUSP = eventoRepo.create(
                    Evento(
                        titulo = "Volta da USP",
                        descricao = "A tradicional corrida de rua da Universidade de São Paulo está de volta! Participe do percurso de 5km ou 10km pelas ruas da Cidade Universitária. Evento aberto para a comunidade USP e público geral.",
                        dataHora = LocalDateTime.of(2025, 10, 26, 7, 0), // Data e hora mais realistas para o evento real.
                        localizacao = "Largada em frente ao CEPEUSP, Praça 2, Prof. Rubião Meira, 61, Cidade Universitária, São Paulo - SP",
                        categoria = "Esportivo",
                        organizador = organizador1,
                        numeroLikes = 185
                    )
                )

                val eventoHackathon = eventoRepo.create(
                    Evento(
                        titulo = "Hackathon de IA do IME",
                        descricao = "24 horas de programação, inovação e colaboração. Forme a sua equipa e crie uma solução de IA para resolver um problema real.",
                        dataHora = LocalDateTime.now().plusDays(25).withHour(9).withMinute(0),
                        localizacao = "Auditório Jacy Monteiro - Bloco B do IME-USP",
                        categoria = "Tecnologia",
                        organizador = organizador2,
                        numeroLikes = 0
                    )
                )

                // --- 3. Adicionar Imagens aos Eventos ---
                imagemRepo.create(
                    ImagemEvento(
                        eventoId = eventoShow.id!!,
                        url = "https://marcaspelomundo.com.br/wp-content/uploads/2023/04/image-3-1024x682.png",
                        descricao = "Palco principal do evento",
                        ordem = 1
                    )
                )
                imagemRepo.create(
                    ImagemEvento(
                        eventoId = eventoNatacao.id!!,
                        url = "https://prefeitura.sp.gov.br/documents/d/esportes/img_7930-jpg",
                        descricao = "",
                        ordem = 1
                    )
                )
                imagemRepo.create(
                    ImagemEvento(
                        eventoId = eventoFut.id!!,
                        url = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSABqV-VQBSF1Qyj1Qyo6RiLfpA0McI1leTvQ&s",
                        descricao = "Equipas a trabalhar durante o evento",
                        ordem = 1
                    )
                )

                imagemRepo.create(
                    ImagemEvento(
                        eventoId = eventoFesta.id!!,
                        url = "https://live.staticflickr.com/3815/9048306470_6f48265335_b.jpg",
                        descricao = "Equipas a trabalhar durante o evento",
                        ordem = 1
                    )
                )

                imagemRepo.create(
                    ImagemEvento(
                        eventoId = eventoVoltaUSP.id!!,
                        url = "https://prip.usp.br/wp-content/uploads/sites/1128/2024/09/bannersitepripcelular_voltausp2024.jpg",
                        descricao = "Equipas a trabalhar durante o evento",
                        ordem = 1
                    )
                )
                imagemRepo.create(
                    ImagemEvento(
                        eventoId = eventoHackathon.id!!,
                        url = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRsOExShlNUBm-J_-wnG-GhzCPbxNT-rowNaQ&s",
                        descricao = "Equipas a trabalhar durante o evento",
                        ordem = 1
                    )
                )

                println("Povoamento da base de dados concluído.")
//            } else {
//                println("A base de dados já contém dados. Povoamento ignorado.")
//            }
        }
    }
}
