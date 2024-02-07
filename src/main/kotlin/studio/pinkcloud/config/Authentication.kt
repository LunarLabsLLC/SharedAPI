package studio.pinkcloud.config

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import studio.pinkcloud.business.repository.AgentAuthRepository
import studio.pinkcloud.business.repository.DirectAuthRepository
import studio.pinkcloud.module.agentauth.config.configureAPIKeyAuth
import studio.pinkcloud.module.agentauth.config.configureAPIKeyRoutes
import studio.pinkcloud.module.directauth.config.configureDirectAuth
import studio.pinkcloud.module.directauth.config.configureDirectAuthRoutes
import studio.pinkcloud.module.directauth.config.configureDirectAuthSessions

fun Application.configureAuth() {
  val directAuthRepo = DirectAuthRepository
  val agentAuthRepo = AgentAuthRepository

  // Necessary plugins
  configureDirectAuthSessions(API_CONFIG, directAuthRepo)

  // Authentication itself
  install(Authentication) {
    configureAPIKeyAuth(agentAuthRepo)
    configureDirectAuth(directAuthRepo)
  }

  // Routes
  configureAPIKeyRoutes(agentAuthRepo)
  configureDirectAuthRoutes(directAuthRepo)
}
