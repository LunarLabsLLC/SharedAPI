package studio.pinkcloud.module.agentauth.config

import io.ktor.server.application.Application
import io.ktor.server.auth.AuthenticationConfig
import studio.pinkcloud.lib.type.HttpError
import studio.pinkcloud.lib.type.get
import studio.pinkcloud.module.agentauth.business.repository.IAgentAuthRepository
import studio.pinkcloud.module.agentauth.controller.proxyAuthRoutes
import studio.pinkcloud.module.agentauth.lib.type.IAgentAuthSession

fun <T : IAgentAuthSession> AuthenticationConfig.configureAPIKeyAuth(authRepo: IAgentAuthRepository<T>) {
  api("api-key-auth") {
    validate { c -> authRepo.validateApplication(c) }
    challenge { throw HttpError.InvalidCredentials.get() }
  }
}

inline fun <reified T : IAgentAuthSession> Application.configureAPIKeyRoutes(authRepo: IAgentAuthRepository<T>) {
  proxyAuthRoutes(authRepo, "proxy")
}
