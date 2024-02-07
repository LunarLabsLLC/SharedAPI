package studio.pinkcloud.config

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import studio.pinkcloud.business.repository.ApiKeyAuthRepository
import studio.pinkcloud.business.repository.DirectAuthRepository
import studio.pinkcloud.business.repository.ProxiedAuthRepository
import studio.pinkcloud.lib.type.AgentSession
import studio.pinkcloud.module.apikeyauth.config.configureAPIKeyAuth
import studio.pinkcloud.module.directauth.config.configureDirectAuth
import studio.pinkcloud.module.directauth.config.configureDirectAuthRoutes
import studio.pinkcloud.module.directauth.config.configureDirectAuthSessions
import studio.pinkcloud.module.proxiedauth.controller.proxiedAuthRoutes

fun Application.configureAuth() {
  val directAuthRepo = DirectAuthRepository
  val apiKeyAuthRepo = ApiKeyAuthRepository
  val proxiedAuthRepo = ProxiedAuthRepository

  // Necessary plugins
  configureDirectAuthSessions<AgentSession>(API_CONFIG)

  // Authentication itself
  install(Authentication) {
    configureAPIKeyAuth(apiKeyAuthRepo)
    configureDirectAuth(directAuthRepo)
  }

  // Routes
  proxiedAuthRoutes(proxiedAuthRepo, "proxy", "api-key-auth")
  configureDirectAuthRoutes(directAuthRepo)
}
