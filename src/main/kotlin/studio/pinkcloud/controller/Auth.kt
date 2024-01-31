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
import studio.pinkcloud.helpers.hasValidSession
import studio.pinkcloud.helpers.isValid
import studio.pinkcloud.helpers.respondOk
import studio.pinkcloud.lib.type.AgentSession
import studio.pinkcloud.lib.type.HttpError
import studio.pinkcloud.lib.type.get

fun Application.authRoutes() {
  sessionRoutes()
  oAuth2Routes()
}

fun Application.sessionRoutes() {
  routing {
    post("/auth/register") {
      val json = call.receive<JsonObject>()
      val username = json["username"]?.jsonPrimitive
      val email = json["email"]?.jsonPrimitive
      val password = json["password"]?.jsonPrimitive
      if (username.isValid() && email.isValid() && password.isValid()) {
        AuthService.register(username!!.content, email!!.content, password!!.content)
          .takeIf { !call.hasValidSession() }
          ?.also { AuthService.saveSession(it) }
          ?.also { call.sessions.set(it) }

        call.respondOk()
      } else {
        throw HttpError.BadRequest.get()
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
