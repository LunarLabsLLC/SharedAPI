package studio.pinkcloud.module

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.session
import io.ktor.server.sessions.SessionTransportTransformerEncrypt
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import studio.pinkcloud.business.service.AuthService
import studio.pinkcloud.config.API_CONFIG
import studio.pinkcloud.controller.authRoutes
import studio.pinkcloud.helpers.hasValidSession
import studio.pinkcloud.lib.type.AgentSession
import studio.pinkcloud.lib.type.HttpError
import studio.pinkcloud.lib.type.get
import studio.pinkcloud.module.authentication.json
import java.security.SecureRandom

fun Application.configureAuth() {
  install(Sessions) {
    cookie<AgentSession>("user_session") {
      cookie.extensions["SameSite"] = "lax"
      cookie.maxAgeInSeconds = 86400
      cookie.secure = API_CONFIG.developmentEnv == "production"
      cookie.path = "/"
      transform(
        SessionTransportTransformerEncrypt(
          API_CONFIG.cookieSecrets.encryption.toByteArray(),
          API_CONFIG.cookieSecrets.sign.toByteArray(),
          ivGenerator = { SecureRandom().generateSeed(16) },
        ),
      )
    }
  }

  install(Authentication) {
    json("auth-json") {
      validate { c ->
        if (call.hasValidSession()) {
          throw HttpError.AlreadyLoggedIn.get()
        }

        AuthService
          .login(c.name, c.password)
          ?.also(
            call.sessions::set,
          )
      }
      challenge { throw HttpError.InvalidCredentials.get() }
    }

    session<AgentSession>("auth-session") {
      validate { session -> session.takeIf { AuthService.validateSession(session) } }
      challenge { throw HttpError.InvalidSession.get() }
    }
  }

  authRoutes()
}
