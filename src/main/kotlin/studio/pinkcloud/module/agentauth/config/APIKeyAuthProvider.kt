package studio.pinkcloud.module.agentauth.config

import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.auth.AuthenticationContext
import io.ktor.server.auth.AuthenticationProvider
import io.ktor.server.request.header

fun AuthenticationConfig.api(
  name: String? = null,
  configure: APIKeyAuthConfig.() -> Unit,
) {
  APIKeyAuthConfig(name)
    .apply(configure)
    .let(::APIKeyAuthProvider)
    .let { register(it) }
}

class APIKeyAuthConfig(name: String?) : AuthenticationProvider.Config(name) {
  internal var validationLambda: (suspend APIKeyAuthValidationContext.(String) -> Boolean)? = null
  internal var challengeLambda: (suspend APIKeyAuthChallengeContext.(String?) -> Unit)? = null

  fun validate(lambda: suspend APIKeyAuthValidationContext.(String) -> Boolean) {
    validationLambda = lambda
  }

  fun challenge(lambda: suspend APIKeyAuthChallengeContext.(String?) -> Unit) {
    challengeLambda = lambda
  }
}

class APIKeyAuthValidationContext(val call: ApplicationCall)

class APIKeyAuthChallengeContext(val call: ApplicationCall)

class APIKeyAuthProvider(private val config: APIKeyAuthConfig) : AuthenticationProvider(config) {
  override suspend fun onAuthenticate(context: AuthenticationContext) {
    val key = context.call.request.header("X-Api-Key")

    if (key != null) {
      val result = config.validationLambda?.invoke(APIKeyAuthValidationContext(context.call), key)
      if (result != null && result) return
    }

    config.challengeLambda?.invoke(APIKeyAuthChallengeContext(context.call), key)
  }
}
