package studio.pinkcloud.module.agentauth.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import studio.pinkcloud.helpers.respondOk
import studio.pinkcloud.lib.type.HttpError
import studio.pinkcloud.lib.type.get
import studio.pinkcloud.module.agentauth.business.repository.IAgentAuthRepository
import studio.pinkcloud.module.agentauth.lib.type.IAgentAuthSession
import studio.pinkcloud.module.directauth.helpers.isValid

inline fun <reified T : IAgentAuthSession> Application.proxyAuthRoutes(
  authRepo: IAgentAuthRepository<T>,
  baseRoute: String,
) {
  routing {
    post("$baseRoute/auth/register") {
      val json = call.receive<JsonObject>()
      val token = json["token"]?.jsonPrimitive
      val username = json["username"]?.jsonPrimitive
      val password = json["password"]?.jsonPrimitive
      if (username.isValid() && token.isValid() && password.isValid()) {
        authRepo.registerAgent(token!!.content, username!!.content, password!!.content)
          ?.also { authRepo.saveSession(it) }
          ?.also {
            call.respond(HttpStatusCode.OK, Json.encodeToString(it))
            return@post
          }

        throw HttpError.InvalidToken.get()
      } else {
        throw HttpError.BadRequest.get()
      }
    }

    post("$baseRoute/auth/login") {
      val json = call.receive<JsonObject>()
      val username = json["username"]?.jsonPrimitive
      val password = json["password"]?.jsonPrimitive
      if (username.isValid() && password.isValid()) {
        authRepo
          .authorizeAgent(username!!.content, password!!.content)
          ?.also { authRepo.saveSession(it) }
          ?.also {
            call.respond(HttpStatusCode.OK, Json.encodeToString(it))
            return@post
          }

        throw HttpError.InvalidCredentials.get()
      } else {
        throw HttpError.BadRequest.get()
      }
    }

    delete("$baseRoute/auth/logout") {
      val json = call.receive<JsonObject>()
      val sessionId = json["sessionId"]?.jsonPrimitive
      if (sessionId.isValid()) {
        authRepo
          .invalidateSession(sessionId!!.content)
          .also {
            call.respondOk()
            return@delete
          }

        throw Error()
      } else {
        throw HttpError.BadRequest.get()
      }
    }

    delete("$baseRoute/auth/logout/all") {
      val json = call.receive<JsonObject>()
      val username = json["username"]?.jsonPrimitive
      if (username.isValid()) {
        authRepo
          .invalidateAllSessions(username!!.content)
          .also {
            call.respondOk()
            return@delete
          }

        throw Error()
      } else {
        throw HttpError.BadRequest.get()
      }
    }
  }
}
