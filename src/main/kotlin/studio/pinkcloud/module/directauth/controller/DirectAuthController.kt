package studio.pinkcloud.module.directauth.controller

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
import io.ktor.server.sessions.set
import studio.pinkcloud.helpers.respondOk
import studio.pinkcloud.lib.type.get
import studio.pinkcloud.module.directauth.business.repository.IDirectAuthRepository
import studio.pinkcloud.module.directauth.lib.type.IDirectAgentSession

inline fun <reified T : IDirectAgentSession> Application.sessionRoutes(
  authRepo: IDirectAuthRepository<T>,
  baseRoute: String,
) {
  routing {
//    post("$baseRoute/auth/register") {
//      val json = call.receive<JsonObject>()
//      val username = json["username"]?.jsonPrimitive
//      val email = json["email"]?.jsonPrimitive
//      val password = json["password"]?.jsonPrimitive
//      if (username.isValid() && email.isValid() && password.isValid()) {
//        authRepo.registerAgent(username!!.content, email!!.content, password!!.content)
//          .takeIf { !call.hasValidSession(authRepo) }
//          ?.also { authRepo.saveSession(it) }
//          ?.also { call.sessions.set(it) }
//
//        call.respondOk()
//      } else {
//        throw HttpError.BadRequest.get()
//      }
//    }

    authenticate("direct-auth-json") {
      post("$baseRoute/auth/login") {
        call.respondOk()
      }
    }

    authenticate("direct-auth-session") {
      patch("$baseRoute/auth/logout") {
        call.principal<T>()?.also {
          authRepo.invalidateSession(it)
          call.sessions.clear<T>()
        }
        call.respondOk()
      }

      put("$baseRoute/auth/logout/all") {
        call.principal<T>()?.also {
          authRepo.invalidateAllSessions(it.agentName)
          call.sessions.clear<T>()
        }
        call.respondOk()
      }
    }
  }
}
