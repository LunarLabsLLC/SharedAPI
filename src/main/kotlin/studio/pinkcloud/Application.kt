@file:Suppress("ktlint:standard:no-wildcard-imports")

package studio.pinkcloud

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*
import studio.pinkcloud.business.AppDbContext
import studio.pinkcloud.business.repository.AuthRepository
import studio.pinkcloud.config.*
import studio.pinkcloud.controller.*
import studio.pinkcloud.module.*
import studio.pinkcloud.module.configureAuth

fun main() {
  embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module).start(wait = true)
}

fun Application.module() {
  loadConfig()

  AppDbContext // Initializes DB

  configureHTTP()
  configureSockets()
  configureSerialization()

  middleware() // Runs before authorization!

  configureAuth(AuthRepository)

  configureRoutes()
}

fun Application.configureRoutes() {
  routing { swaggerUI(path = "/docs") }

  systemRoutes()
}
