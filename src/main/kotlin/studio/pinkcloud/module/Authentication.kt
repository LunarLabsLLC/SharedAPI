package studio.pinkcloud.module

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.session
import io.ktor.server.sessions.SessionTransportTransformerEncrypt
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import studio.pinkcloud.business.service.AuthService
import studio.pinkcloud.config.API_CONFIG
import studio.pinkcloud.controller.authRoutes
import studio.pinkcloud.lib.type.AgentSession
import studio.pinkcloud.module.authentication.AuthenticationException
import studio.pinkcloud.module.authentication.AuthorizationException
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
        AuthService
          .authorizeAgent(c.name, c.password)
          ?.also(call.sessions::set)
      }
      challenge { throw AuthenticationException() }
    }

    session<AgentSession>("auth-session") {
      validate { session -> session.takeIf { AuthService.validateSession(session) } }
      challenge { throw AuthorizationException() }
    }
  }

  authRoutes()
}
