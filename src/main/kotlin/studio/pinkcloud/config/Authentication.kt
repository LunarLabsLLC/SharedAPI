package studio.pinkcloud.config

import io.ktor.server.application.Application
import studio.pinkcloud.business.repository.AuthRepository
import studio.pinkcloud.module.directauth.config.configureDirectAuth

fun Application.configureAuth() {
  configureDirectAuth(API_CONFIG, AuthRepository)
}
