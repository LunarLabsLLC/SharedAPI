package studio.pinkcloud.controller

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.sessions
import studio.pinkcloud.lib.type.UserSession
import studio.pinkcloud.module.authentication.SessionManager
import studio.pinkcloud.module.respondOk

fun Application.authRoutes() {
  sessionRoutes()
  oAuth2Routes()
}

fun Application.sessionRoutes() {
  routing {
    authenticate("auth-json") {
      post("/auth/login") {
        call.respondOk()
      }
    }

    authenticate("auth-session") {
      patch("/auth/logout") {
        call.principal<UserSession>()?.also {
          SessionManager.get().invalidate(it)
          call.sessions.clear<UserSession>()
        }
        call.respondOk()
      }

      put("/auth/logout/all") {
        call.principal<UserSession>()?.also {
          SessionManager.get().invalidateAll(it.username)
          call.sessions.clear<UserSession>()
        }
        call.respondOk()
      }
    }
  }
}

fun Application.oAuth2Routes() { } // TODO: Implement
