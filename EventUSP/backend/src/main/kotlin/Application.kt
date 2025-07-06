package br.usp.eventUSP

import br.usp.eventUSP.model.LocalDateTimeSerializer // Importe seu serializador
import br.usp.eventUSP.config.DatabaseConfig
import br.usp.eventUSP.database.DatabaseSeeder
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import java.time.LocalDateTime

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    // Configura Plugin para serialização (transformação de objetos em JSON)
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true

            // A LINHA MAIS IMPORTANTE: Registra o serializador para o tipo LocalDateTime
            serializersModule = SerializersModule {
                contextual(LocalDateTime::class, LocalDateTimeSerializer)
            }
        })
    }

    configureHTTP()

    // Inicializa o banco de dados
    DatabaseConfig.init()

    DatabaseSeeder.init()

    configureSecurity()
    configureRouting()
}
