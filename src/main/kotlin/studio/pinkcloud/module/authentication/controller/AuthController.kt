package studio.pinkcloud.module.authentication.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.json.JsonObject
import studio.pinkcloud.lib.type.UserSession
import studio.pinkcloud.module.authentication.SessionManager
import studio.pinkcloud.module.authentication.lib.IUserSession

inline fun <reified T: IUserSession>Application.authRoutes() {
    sessionRoutes<T>()
    oAuth2Routes<T>()
}

inline fun <reified T: IUserSession>Application.sessionRoutes() {
    routing {
        authenticate("auth-json") {
            post("/auth/login") {
                call.respond(HttpStatusCode.Accepted, "Success")
            }
        }

        authenticate("auth-session") {
            patch("/auth/logout") {
                call.principal<T>()?.also {
                    SessionManager.get<T>().invalidate(it)
                    call.sessions.clear<T>()
                }

                call.respond(HttpStatusCode.OK, "Success")
            }

            put("/auth/logout/all") {
                call.principal<T>()?.also {
                    SessionManager.get<T>().invalidateAll(it.username)
                    call.sessions.clear<T>()
                }

                call.respond(HttpStatusCode.OK, "Success")
            }
        }
    }
}

inline fun <reified T: IUserSession>Application.oAuth2Routes() { } // TODO: Implement