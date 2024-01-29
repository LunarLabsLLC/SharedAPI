package studio.pinkcloud.controller

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import io.ktor.server.application.*
import studio.pinkcloud.lib.type.UserSession
import studio.pinkcloud.module.authentication.SessionManager

inline fun Application.authRoutes() {
    sessionRoutes()
    oAuth2Routes()
}

inline fun Application.sessionRoutes() {
    routing {
        authenticate("auth-json") {
            post("/auth/login") {
                call.respond(HttpStatusCode.Accepted, "Success")
            }
        }

        authenticate("auth-session") {
            patch("/auth/logout") {
                call.principal<UserSession>()?.also {
                    SessionManager.get().invalidate(it)
                    call.sessions.clear<UserSession>()
                }

                call.respond(HttpStatusCode.OK, "Success")
            }

            put("/auth/logout/all") {
                call.principal<UserSession>()?.also {
                    SessionManager.get().invalidateAll(it.username)
                    call.sessions.clear<UserSession>()
                }

                call.respond(HttpStatusCode.OK, "Success")
            }
        }
    }
}

inline fun Application.oAuth2Routes() { } // TODO: Implement