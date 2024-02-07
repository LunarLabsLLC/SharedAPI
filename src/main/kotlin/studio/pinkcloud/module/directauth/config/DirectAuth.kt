package studio.pinkcloud.module.directauth.config

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.auth.session
import io.ktor.server.sessions.SessionTransportTransformerEncrypt
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import io.ktor.util.hex
import studio.pinkcloud.lib.type.HttpError
import studio.pinkcloud.lib.type.get
import studio.pinkcloud.module.directauth.business.repository.IDirectAuthRepository
import studio.pinkcloud.module.directauth.controller.sessionRoutes
import studio.pinkcloud.module.directauth.helpers.hasValidSession
import studio.pinkcloud.module.directauth.lib.type.IDirectAgentSession

inline fun <reified T : IDirectAgentSession> Application.configureDirectAuthSessions(
  config: IDirectAuthConfig,
  authRepo: IDirectAuthRepository<T>,
) {
  install(Sessions) {
    cookie<T>("user_session") {
      cookie.extensions["SameSite"] = "lax"
      cookie.maxAgeInSeconds = 86400
      cookie.secure = config.developmentEnv == "production"
      cookie.path = "/"

      val secretEncryptKey = hex(config.cookieSecrets.encryption)
      val secretSignKey = hex(config.cookieSecrets.sign)
      transform(
        SessionTransportTransformerEncrypt(secretEncryptKey, secretSignKey),
      )
    }
  }
}

inline fun <reified T : IDirectAgentSession> AuthenticationConfig.configureDirectAuth(authRepo: IDirectAuthRepository<T>) {
  session<T>("direct-auth-session") {
    validate { session -> session.takeIf { authRepo.validateSession(session) } }
    challenge { throw HttpError.InvalidSession.get() }
  }

  json("direct-auth-json") {
    validate { c ->
      if (call.hasValidSession(authRepo)) {
        throw HttpError.AlreadyLoggedIn.get()
      }

      authRepo
        .authorizeAgent(c.name, c.password)
        ?.also { authRepo.saveSession(it) }
        ?.also { call.sessions.set(it) }
    }
    challenge { throw HttpError.InvalidCredentials.get() }
  }
}

inline fun <reified T : IDirectAgentSession> Application.configureDirectAuthRoutes(authRepo: IDirectAuthRepository<T>) {
  sessionRoutes(authRepo, "session")
}
