package studio.pinkcloud.module

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

fun Application.middleware() {
    intercept(ApplicationCallPipeline.Call) {
        try {
            proceed()
        } catch (e: ContentTransformationException) {
            System.err.println(e)
            call.respond(HttpStatusCode.BadRequest, "Invalid request body.")
        } catch (e: Exception) {
            System.err.println(e)
            call.respond(HttpStatusCode.InternalServerError, "An unrecognized server error has occurred.")
        }
    }
}