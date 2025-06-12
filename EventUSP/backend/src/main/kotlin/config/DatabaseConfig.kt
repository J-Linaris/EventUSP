package br.usp.eventUSP.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import br.usp.eventUSP.database.tables.*
import io.ktor.server.config.*
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger

/**
 * Configuração do banco de dados MySQL para o sistema EventUSP
 */
object DatabaseConfig {
    /**
     * Inicializa a conexão com o banco de dados e cria as tabelas necessárias
     */
    fun init() {
        println("Iniciando configuração do banco de dados...")
        // Configura o HikariCP para gerenciamento de pool de conexões
        val config = HikariConfig().apply {
            driverClassName = "com.mysql.cj.jdbc.Driver"
            jdbcUrl = "jdbc:mysql://localhost:3306/eventusp?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
            username = "root" // Altere para o seu usuário do MySQL
            password = "Root1234!" // Altere para a sua senha do MySQL
            maximumPoolSize = 10
            connectionTestQuery = "SELECT 1"
            validationTimeout = 3000
            connectionTimeout = 5000
        }

        // Conecta ao banco de dados
        println("Configurando HikariCP...")
        val dataSource = HikariDataSource(config)

        println("Conectando com Exposed...")
        Database.connect(dataSource)
        
        // Cria as tabelas no banco de dados
        println("Criando tabelas...")
        transaction {
            addLogger(StdOutSqlLogger) // Adiciona logs SQL no console
            SchemaUtils.create(
                EventoTable,
                UsuarioOrganizadorTable,
                UsuarioParticipanteTable,
                ReviewTable,
                ImagemEventoTable,
                ParticipantesInteressadosTable
            )
        }
        println("Banco de dados configurado com sucesso.")
    }

}