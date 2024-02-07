package studio.pinkcloud.module.apikeyauth.config

import io.ktor.server.auth.AuthenticationConfig
import studio.pinkcloud.lib.type.HttpError
import studio.pinkcloud.lib.type.get
import studio.pinkcloud.module.apikeyauth.business.repository.IApiKeyAuthRepository

fun AuthenticationConfig.configureAPIKeyAuth(authRepo: IApiKeyAuthRepository) {
  api("api-key-auth") {
    validate { c -> authRepo.validateApplication(c) }
    challenge { throw HttpError.InvalidApiKey.get() }
  }
}
