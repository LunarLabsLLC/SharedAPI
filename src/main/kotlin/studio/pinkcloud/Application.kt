@file:Suppress("ktlint:standard:no-wildcard-imports")

package studio.pinkcloud

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*
import studio.pinkcloud.business.AppDbContext
import studio.pinkcloud.config.*
import studio.pinkcloud.config.configureAuth
import studio.pinkcloud.controller.*

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

  configureAuth()

  configureRoutes()
}

fun Application.configureRoutes() {
  routing { swaggerUI(path = "/docs") }

  systemRoutes()
}
