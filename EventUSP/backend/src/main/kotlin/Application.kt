package br.usp.eventUSP

import br.usp.eventUSP.config.DatabaseConfig
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    // Inicializa o banco de dados
    DatabaseConfig.init()

    configureSecurity()
    configureHTTP()
    configureRouting()
}
