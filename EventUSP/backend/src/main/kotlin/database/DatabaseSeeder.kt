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
import br.usp.eventUSP.repository.ReviewRepository
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

                // --- Criar Organizadores ---
                val organizadorRepo = UsuarioOrganizadorRepository()
                val participanteRepo = UsuarioParticipanteRepository()

                var organizador1 = UsuarioOrganizador()
                organizador1.nome = "Centro Cultural USP"
                organizador1.email = "ccusp@usp.br"
                organizador1.senha = "senha123"
                organizador1.fotoPerfil = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSb7Cn7TcVdTYSdpbMIqDHLA8fX6-ro14BvFw&s"
                organizador1 = organizadorRepo.create(organizador1)

                var organizadorFAU = UsuarioOrganizador()
                organizadorFAU.email = "atleticafau@usp.br"
                organizadorFAU.nome = "Atlética FAUUSP"
                organizadorFAU.senha = "senha123"
                organizadorFAU.fotoPerfil = "https://d106p58duwuiz5.cloudfront.net/agency/6dbe8332a1e41d45fdbb9e30c7f56b14.png"
                organizadorFAU = organizadorRepo.create(organizadorFAU)

                var organizadorAAAMAT = UsuarioOrganizador()
                organizadorAAAMAT.nome = "AAAMAT"
                organizadorAAAMAT.email = "aaamat@gmail.com"
                organizadorAAAMAT.senha = "imeuspatemorrer"
                organizadorAAAMAT.fotoPerfil = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSKvG87Uzy9fvNMYWR7dN2g95Jwr33tTcYm7Q&s"
                organizadorAAAMAT = organizadorRepo.create(organizadorAAAMAT)

                // --- Criar Participantes ---
                var participanteMessi = UsuarioParticipante(
                    nome = "Lionel Messi",
                    email = "messinho@yahoo.com",
                    senha = "leo123"
                )
                participanteMessi = participanteRepo.create(participanteMessi)

                var participanteCristiano = UsuarioParticipante(
                    nome = "Cristiano Ronaldo",
                    email = "cr7@gmail.com",
                    senha = "cr777"
                )
                participanteCristiano = participanteRepo.create(participanteCristiano)

                var participanteNeymar = UsuarioParticipante(
                    nome = "Neymar Jr",
                    email = "meninoney@gmail.com",
                    senha = "ney10"
                )
                participanteNeymar = participanteRepo.create(participanteNeymar)

                // --- Criar Eventos ---
                val eventoRepo = EventoRepository()
                val imagemRepo = ImagemEventoRepository()

            val eventoJunIME = eventoRepo.create(
                Evento(
                    titulo = "JunIME",
                    descricao = "VEM AI! ✨ Dia 13/06 das 18h às 23h no estacionamento do bloco B do IME teremos a nossa tradicional festa junina, a JUNIME!!\n" +
                            "Nossos amados times e entidades imeanas se juntarão à aaamat para fazermos uma linda festa junina com muitas comidinhas, brincadeira e\n" +
                            "diversão! ❤\uFE0F\uD83E\uDD0D❤\uFE0F\uD83E\uDD0D\n" +
                            "Aguardamos vocês todes a caráter e muito alegres para curtimos muito! \uD83E\uDD29\uD83E\uDD20",
                    // Coloca para o dia anterior para permitir que as reviews sejam cadastradas
//                        dataHora = LocalDateTime.now().minusDays(1),
                    dataHora = LocalDateTime.now().plusDays(10).withHour(18).withMinute(0),
                    localizacao = "Estacionamento do bloco B do IME",
                    categoria = "Festa",
                    organizador = organizadorAAAMAT,
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
                        numeroLikes = 10
                    )
                )
            val eventoFEAFU = eventoRepo.create(
                Evento(
                    titulo = "FEAFU",
                    descricao = "FEAFAU: onde dois mundos se misturam e tudo pode acontecer\n" +
                            "\n" +
                            "\uD83D\uDCC6dia:08/08\n" +
                            "⏰horário: 23h00 - 6h00\n" +
                            "\uD83E\uDD42open bar\n" +
                            "\n" +
                            "_se prepare para a união do ano _\n" +
                            "abertura das vendas 06/07 pela blacktag ou com um de nossos vendedores \uD83D\uDC99\uD83D\uDC9C\n" +
                            "\n" +
                            "FEA:\n" +
                            "Helena: 11 99366-9089\n" +
                            "Bia: 11 96068-9807\n" +
                            "Marco: \u202A11 98154‑9523\u202C\n" +
                            "\n" +
                            "FAU:\n" +
                            "ju carvalho: \u202A+55 11 97121‑9977\u202C\n" +
                            "ju pellici:\u202A+55 71 98436‑0550\u202C\n" +
                            "mel:\u202A+55 13 99700‑5306",
                    dataHora = LocalDateTime.parse("2025-08-08T23:00:00"),
                    localizacao = "Hey Hey Club - Rua Marquês de Itu, 284",
                    categoria = "Festa",
                    organizador = organizadorFAU,
                    numeroLikes = 119
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
            val eventoShow = eventoRepo.create(
                Evento(
                    titulo = "Show de Talentos CCUSP",
                    descricao = "Uma noite para celebrar os talentos musicais da nossa comunidade. Venha assistir e apoiar os artistas locais!",
                    dataHora = LocalDateTime.now().plusDays(10).withHour(19).withMinute(0),
                    localizacao = "Centro Cultural USP - R. do Anfiteatro, 109",
                    categoria = "Cultural",
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
                        titulo = "HackFools",
                        descricao = "24 horas de programação, inovação e colaboração. Forme a sua equipa e crie uma solução de IA para resolver um problema real.",
                        dataHora = LocalDateTime.now().plusDays(25).withHour(9).withMinute(0),
                        localizacao = "Auditório Jacy Monteiro - Bloco B do IME-USP",
                        categoria = "Tecnologia",
                        organizador = organizador1,
                        numeroLikes = 0
                    )
                )

                // --- Adicionar Imagens aos Eventos ---
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
                        eventoId = eventoJunIME.id!!,
                        url = "https://pbs.twimg.com/media/GOtMekyW4AEUKwZ?format=jpg&name=large",
                        descricao = "Capa",
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
                imagemRepo.create(
                    ImagemEvento(
                        eventoId = eventoJunIME.id!!,
                        url = "https://f.i.uol.com.br/fotografia/2022/06/29/165652972362bca33b8a1d8_1656529723_3x2_md.jpg",
                        descricao = "Mapa",
                        ordem = 2
                    )
                )

                imagemRepo.create(
                    ImagemEvento(
                        eventoId = eventoJunIME.id!!,
                        url = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRNNKlZSCrMLvQA-bN0H6eVL2spgGzlIJ0PAA&s",
                        descricao = "Preços parte 1",
                        ordem = 3
                    )
                )

                imagemRepo.create(
                    ImagemEvento(
                        eventoId = eventoFEAFU.id!!,
                        url = "https://d106p58duwuiz5.cloudfront.net/event/cover/e699c29fe6974541d7160a749161d382.png",
                        descricao = "Capa",
                        ordem = 1
                    )
                )


                // --- Adicionar reviews ---
            val reviewRepository = ReviewRepository()

            participanteMessi.demonstrarInteresse(eventoJunIME)
            var reviewMessi = participanteMessi.adicionarReview(
                    evento = eventoJunIME,
                    nota = 5,
                    comentario = "Muito bom, a palha italiana da batimeduca estava uma delícia!"
                )
            // CORREÇÃO: Salva a review no banco de dados
            if (reviewMessi != null) {
                reviewRepository.create(reviewMessi)
            }

            participanteCristiano.demonstrarInteresse(eventoJunIME)
            var reviewCristiano = participanteCristiano.adicionarReview(
                evento = eventoJunIME,
                nota = 0,
                comentario = "Um lixo, sem música e com a quadrilha mais triste que eu já vi"
            )
            // CORREÇÃO: Salva a review no banco de dados
            if (reviewCristiano != null) {
                reviewRepository.create(reviewCristiano)
            }


            participanteNeymar.demonstrarInteresse(eventoJunIME)
            var reviewNeymar = participanteNeymar.adicionarReview(
                evento = eventoJunIME,
                nota = 3,
                comentario = "O ime é o ime do ime"
            )
            // CORREÇÃO: Salva a review no banco de dados
            if (reviewNeymar != null) {
                reviewRepository.create(reviewNeymar)
            }

                // Volta para a data real
                eventoJunIME.dataHora = LocalDateTime.parse("2026-07-13T18:00:00")
                eventoJunIME.numeroLikes = eventoJunIME.participantesInteressados.size
                eventoRepo.update(eventoJunIME)

//                eventoVoltaUSP.numeroLikes = 185
//                eventoRepo.update(eventoVoltaUSP)
//
//                eventoFEAFU.numeroLikes = 119
//                eventoRepo.update(eventoFEAFU)
                println("Povoamento da base de dados concluído.")
//            } else {
//                println("A base de dados já contém dados. Povoamento ignorado.")
//            }
        }
    }
}
