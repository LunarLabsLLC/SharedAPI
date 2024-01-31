package studio.pinkcloud.controller

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import studio.pinkcloud.business.service.AuthService
import studio.pinkcloud.helpers.isValid
import studio.pinkcloud.lib.type.AgentSession
import studio.pinkcloud.module.authentication.BadRequestException
import studio.pinkcloud.module.respondOk

fun Application.authRoutes() {
  sessionRoutes()
  oAuth2Routes()
}

fun Application.sessionRoutes() {
  routing {
    post("/auth/register") {
      val json = call.receive<JsonObject>()
      val username = json.get("username")?.jsonPrimitive
      val email = json.get("email")?.jsonPrimitive
      val password = json.get("password")?.jsonPrimitive
      if (username.isValid() && email.isValid() && password.isValid()) {
        AuthService.registerAgent(username!!.content, email!!.content, password!!.content).also(call.sessions::set)
        call.respondOk()
      } else {
        throw BadRequestException()
      }
    }

    authenticate("auth-json") {
      post("/auth/login") {
        call.respondOk()
      }
    }

    authenticate("auth-session") {
      patch("/auth/logout") {
        call.principal<AgentSession>()?.also {
          AuthService.invalidateSession(it)
          call.sessions.clear<AgentSession>()
        }
        call.respondOk()
      }

      put("/auth/logout/all") {
        call.principal<AgentSession>()?.also {
          AuthService.invalidateAll(it.agentName)
          call.sessions.clear<AgentSession>()
        }
        call.respondOk()
      }
    }
  }
}

fun Application.oAuth2Routes() { } // TODO: Implement
