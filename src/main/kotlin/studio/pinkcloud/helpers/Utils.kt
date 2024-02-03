package studio.pinkcloud.helpers

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import io.ktor.server.sessions.get

suspend inline fun ApplicationCall.respondOk() {
  respond(HttpStatusCode.OK, mapOf("messages" to listOf("Success")))
}
