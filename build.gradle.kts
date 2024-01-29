val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val ktoml_version: String by project

plugins {
    kotlin("jvm") version "1.9.22"
    id("io.ktor.plugin") version "2.3.7"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"
}

group = "studio.pinkcloud"
version = "0.0.1"

application {
    mainClass.set("studio.pinkcloud.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    //// Core
    // Components for building a server
    implementation("io.ktor:ktor-server-core-jvm")
    // Components for building a client
    implementation("io.ktor:ktor-client-core-jvm")

    //// Core add-ons
    // Uses Netty as the server engine
    implementation("io.ktor:ktor-server-netty-jvm")
    // HTTP client implementation using Apache HttpClient.
    implementation("io.ktor:ktor-client-apache-jvm")
    // IF YOU'RE SEEING THIS BECAUSE YOU WANT TO SERIALIZE TO JSONS MORE CONVENIENTLY, check this:
    // https://medium.com/@jurajkunier/kotlinx-json-vs-gson-4ba24a21bd73

    //// Configuration
    // TOML decoding library
    implementation("com.akuleshov7:ktoml-core:$ktoml_version")
    // Handling TOML files
    implementation("com.akuleshov7:ktoml-file:$ktoml_version")
    // Streaming
    implementation("com.akuleshov7:ktoml-source:$ktoml_version")

    //// Authentication
    // Features for the Ktor server
    implementation("io.ktor:ktor-server-auth-jvm")
    // Session support
    implementation("io.ktor:ktor-server-sessions-jvm")
    // WebSocket support
    implementation("io.ktor:ktor-server-websockets-jvm")

    //// Headers
    // Automatically generates HEAD responses for server routes
    implementation("io.ktor:ktor-server-auto-head-response-jvm")
    // Sets default headers for server responses
    implementation("io.ktor:ktor-server-default-headers-jvm")
    // Handles the Forwarded header in the server (for reverse proxy)
    implementation("io.ktor:ktor-server-forwarded-header-jvm")
    // Enables CORS support
    implementation("io.ktor:ktor-server-cors-jvm")

    //// Documentation
    // Swagger support
    implementation("io.ktor:ktor-server-swagger-jvm")

    //// Serialization
    // Provides content negotiation for server responses.
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    // Support for Kotlinx Serialization with JSON
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")

    //// Logging
    // Logback Classic
    implementation("ch.qos.logback:logback-classic:$logback_version")

    //// Data storage
    // MongoDB Kotlin Coroutine support (db)
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:4.11.0")

    //// Testing
    // Adds Server Tests
    testImplementation("io.ktor:ktor-server-tests-jvm")
    // Adds JUnit support
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
