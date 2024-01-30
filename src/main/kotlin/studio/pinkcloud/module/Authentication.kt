package studio.pinkcloud.module

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.sessions.*
import studio.pinkcloud.config.API_CONFIG
import studio.pinkcloud.controller.authRoutes
import studio.pinkcloud.lib.type.UserSession
import studio.pinkcloud.module.authentication.AuthenticationException
import studio.pinkcloud.module.authentication.AuthorizationException
import studio.pinkcloud.module.authentication.SessionManager
import studio.pinkcloud.module.authentication.json
import studio.pinkcloud.module.authentication.lib.IAuthRepository
import studio.pinkcloud.module.authentication.lib.IUserSession
import java.security.SecureRandom

inline fun <reified T : IUserSession> Application.configureAuth(authRepository: IAuthRepository) {
  SessionManager.init(authRepository)

  install(Sessions) {
    cookie<T>("user_session") {
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
        (
          SessionManager.get()
            .authorize(c.name, c.password, ::UserSession)
        )
          .also(call.sessions::set)
      }
      challenge { throw AuthenticationException() }
    }

    session<T>("auth-session") {
      validate { session -> session.takeIf { SessionManager.get().validate(session) } }
      challenge { throw AuthorizationException() }
    }
  }

  authRoutes()
}
