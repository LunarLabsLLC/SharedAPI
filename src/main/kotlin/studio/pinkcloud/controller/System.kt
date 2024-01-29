package studio.pinkcloud.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.systemRoutes() {
    routing {
        get("/isitworking") {
            call.respond(HttpStatusCode.OK, call.receiveText())
        }
    }
}