package studio.pinkcloud.module.directauth.helpers

import io.ktor.server.application.ApplicationCall
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import kotlinx.serialization.json.JsonPrimitive
import studio.pinkcloud.module.directauth.business.repository.IDirectAuthRepository
import studio.pinkcloud.module.directauth.lib.type.IAgentSession

suspend inline fun <reified T : IAgentSession> ApplicationCall.hasValidSession(authRepo: IDirectAuthRepository<T>): Boolean {
  return sessions.get<T>().let { it != null && authRepo.validateSession(it) }
}

fun JsonPrimitive?.isValid() = this?.isString == true && this.content.isNotEmpty()
