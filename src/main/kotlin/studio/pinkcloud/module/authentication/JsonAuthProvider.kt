package studio.pinkcloud.module.authentication

import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.auth.AuthenticationContext
import io.ktor.server.auth.AuthenticationProvider
import io.ktor.server.auth.Principal
import io.ktor.server.auth.UserPasswordCredential
import io.ktor.server.request.receive
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import studio.pinkcloud.helpers.isValid

fun AuthenticationConfig.json(
  name: String? = null,
  configure: JsonAuthConfig.() -> Unit,
) {
  JsonAuthConfig(name)
    .apply(configure)
    .let(::JsonAuthProvider)
    .let { register(it) }
}

class JsonAuthConfig(name: String?) : AuthenticationProvider.Config(name) {
  internal var validationLambda: (suspend JsonAuthValidationContext.(UserPasswordCredential) -> Principal?)? = null
  internal var challengeLambda: (suspend JsonAuthChallengeContext.(UserPasswordCredential?) -> Unit)? = null

  fun validate(lambda: suspend JsonAuthValidationContext.(UserPasswordCredential) -> Principal?) {
    validationLambda = lambda
  }

  fun challenge(lambda: suspend JsonAuthChallengeContext.(UserPasswordCredential?) -> Unit) {
    challengeLambda = lambda
  }
}

class JsonAuthValidationContext(val call: ApplicationCall)

class JsonAuthChallengeContext(val call: ApplicationCall)

class JsonAuthProvider(private val config: JsonAuthConfig) : AuthenticationProvider(config) {
  override suspend fun onAuthenticate(context: AuthenticationContext) {
    val json = context.call.receive<JsonObject>()
    val username = json["username"]?.jsonPrimitive
    val password = json["password"]?.jsonPrimitive

    var credentials: UserPasswordCredential? = null
    if (username.isValid() && password.isValid()) {
      credentials = UserPasswordCredential(username!!.content, password!!.content)
      val authenticatedAgent = config.validationLambda?.invoke(JsonAuthValidationContext(context.call), credentials)
      authenticatedAgent?.also {
        context.principal(it)
        return
      }
    }

    config.challengeLambda?.invoke(JsonAuthChallengeContext(context.call), credentials)
  }
}
