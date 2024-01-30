package studio.pinkcloud.module

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.call
import io.ktor.server.request.ContentTransformationException
import io.ktor.server.response.respond
import io.ktor.util.pipeline.PipelineContext
import studio.pinkcloud.module.authentication.CustomHttpException
import java.util.UUID

fun Application.middleware() {
  intercept(ApplicationCallPipeline.Call) {
    tryCatch {
      proceed()
    }
  }
}

suspend fun <TSubject : Any> PipelineContext<TSubject, ApplicationCall>.tryCatch(block: suspend () -> Any?) {
  try {
    block()
  } catch (e: ContentTransformationException) {
    call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid request body."))
  } catch (e: CustomHttpException) {
    call.respond(e.StatusCode, mapOf("message" to e.message))
  } catch (e: Exception) {
    val httpError =
      "An unrecognized server error has occurred. Error Code: " +
        UUID.randomUUID().toString().split('-').first()
    call.respond(HttpStatusCode.InternalServerError, mapOf("message" to httpError))
    System.err.println(CustomHttpException(HttpStatusCode.InternalServerError, httpError, e))
  }
}

suspend inline fun ApplicationCall.respondOk() {
  respond(HttpStatusCode.OK, mapOf("messages" to listOf("Success")))
}
