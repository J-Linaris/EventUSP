package br.usp.eventUSP

import br.usp.eventUSP.config.DatabaseConfig
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
    // Testa se a base de dados consegue ser configurada
    DatabaseConfig.init()
}

fun Application.module() {

    // Configura Plugin para serialização (transformação de objetos em JSON)
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    configureHTTP()

    // Inicializa o banco de dados
    DatabaseConfig.init()

    configureSecurity()
    configureRouting()
}
