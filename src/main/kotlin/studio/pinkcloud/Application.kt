package studio.pinkcloud

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*
import studio.pinkcloud.business.repository.AuthRepository
import studio.pinkcloud.controller.*
import studio.pinkcloud.config.*
import studio.pinkcloud.lib.type.UserSession
import studio.pinkcloud.module.*
import studio.pinkcloud.module.configureAuth

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module).start(wait = true)
}

fun Application.module() {
    loadConfig()

    configureHTTP()
    configureSockets()
    configureSerialization()

    middleware() // Runs before authorization!

    configureAuth<UserSession>(AuthRepository)

    configureRoutes()
}

fun Application.configureRoutes() {
    routing { swaggerUI(path = "/docs") }

    systemRoutes()
}