val ktorVersion = "2.3.7"
val kotlinVersion = "1.9.21"
val logbackVersion = "1.4.11"
val exposedVersion = "0.44.1"
val h2Version = "2.2.224"
val hikariVersion = "5.0.1"
val mysqlVersion = "8.0.33"

plugins {
    kotlin("jvm") version "1.9.21"
    id("io.ktor.plugin") version "2.3.7"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.21"
}

group = "br.usp"
version = "0.0.1"

// Configuração para compatibilidade com Gradle 8.7+
kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

application {
    mainClass.set("br.usp.eventUSP.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor core dependencies
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:${ktorVersion}")
    implementation("io.ktor:ktor-server-auth-jwt:${ktorVersion}")
    implementation("io.ktor:ktor-server-auth-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-cors-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-config-yaml:$ktorVersion")
    implementation("io.ktor:ktor-server-default-headers-jvm:$ktorVersion")

    // Database - Exposed
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")

    // MySQL Database - Usando o conector mais estável para MySQL   8
    implementation("com.mysql:mysql-connector-j:$mysqlVersion")

    // H2 Database (keeping for development/testing)
    implementation("com.h2database:h2:$h2Version")

    // HikariCP - Connection Pooling
    implementation("com.zaxxer:HikariCP:$hikariVersion")

    // Logging
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    // JSON Web Token
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
//    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // Testing
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testImplementation("org.mockito:mockito-core:5.5.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
}

// Usar o estilo moderno de configuração de tarefas para o Gradle 8.7+
tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

// Configuração de compilação do Kotlin para compatibilidade com Gradle 8.7
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xemit-jvm-type-annotations")
        apiVersion = "1.9"
        languageVersion = "1.9"
    }
}

// Configuração adicional para compatibilidade
tasks.named("compileKotlin", org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java) {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

// Configuração de JavaCompile para compatibilidade
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}

// Verificação explícita de tipos para evitar erros de compilação
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.allWarningsAsErrors = false
}

// Configuração adicional para compatibilidade
tasks.named("compileKotlin", org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java) {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

// Configuração de JavaCompile para compatibilidade
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}

// Verificação explícita de tipos para evitar erros de compilação
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.allWarningsAsErrors = false
}

// Configuração adicional para compatibilidade
tasks.named("compileKotlin", org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java) {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

// Configuração de JavaCompile para compatibilidade
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}

// Verificação explícita de tipos para evitar erros de compilação
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.allWarningsAsErrors = false
}
