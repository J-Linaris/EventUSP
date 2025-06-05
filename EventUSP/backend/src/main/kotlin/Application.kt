package br.usp.eventUSP

import br.usp.eventUSP.config.DatabaseConfig
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
    // Testa se a base de dados consegue ser configurada
    DatabaseConfig.init()
}

fun Application.module() {
    configureHTTP()

    // Inicializa o banco de dados
    DatabaseConfig.init()

    configureSecurity()
    configureRouting()
}
