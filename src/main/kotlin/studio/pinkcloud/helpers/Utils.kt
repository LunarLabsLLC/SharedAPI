package studio.pinkcloud.helpers

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import kotlinx.serialization.json.JsonPrimitive
import studio.pinkcloud.business.service.AuthService
import studio.pinkcloud.lib.type.AgentSession

fun JsonPrimitive?.isValid() = this?.isString == true && this.content.isNotEmpty()

suspend inline fun ApplicationCall.respondOk() {
  respond(HttpStatusCode.OK, mapOf("messages" to listOf("Success")))
}

suspend inline fun ApplicationCall.hasValidSession(): Boolean {
  return sessions.get<AgentSession>().let { it != null && AuthService.validateSession(it) }
}
