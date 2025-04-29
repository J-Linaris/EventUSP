package br.usp.eventUSP.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import br.usp.eventUSP.database.tables.*

/**
 * Configuração do banco de dados MySQL para o sistema EventUSP
 */
object DatabaseConfig {
    /**
     * Inicializa a conexão com o banco de dados e cria as tabelas necessárias
     */
    fun init() {
        // Configura o HikariCP para gerenciamento de pool de conexões
        val config = HikariConfig().apply {
            driverClassName = "com.mysql.cj.jdbc.Driver"
            jdbcUrl = "jdbc:mysql://localhost:3306/eventusp?useSSL=false&serverTimezone=UTC"
            username = "root" // Altere para o seu usuário do MySQL
            password = "root" // Altere para a sua senha do MySQL
            maximumPoolSize = 10
        }
        
        // Conecta ao banco de dados
        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)
        
        // Cria as tabelas no banco de dados
        transaction {
            SchemaUtils.create(
                EventoTable,
                UsuarioOrganizadorTable,
                UsuarioParticipanteTable,
                ReviewTable,
                ImagemEventoTable,
                ParticipantesInteressadosTable
            )
        }
    }
}