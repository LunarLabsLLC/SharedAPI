package studio.pinkcloud.config

import io.ktor.server.application.Application
import studio.pinkcloud.business.repository.AgentAuthRepository
import studio.pinkcloud.business.repository.DirectAuthRepository
import studio.pinkcloud.module.agentauth.controller.proxyAuthRoutes
import studio.pinkcloud.module.directauth.config.configureDirectAuth

fun Application.configureAuth() {
  configureDirectAuth(API_CONFIG, DirectAuthRepository)
  proxyAuthRoutes(AgentAuthRepository, "proxy")
}
