package studio.pinkcloud.module.authentication

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

fun AuthenticationConfig.json(
  name: String? = null,
  configure: JsonAuthConfig.() -> Unit,
) {
  val config = JsonAuthConfig(name)
  config.apply(configure)
  val provider = JsonAuthProvider(config)
  register(provider)
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

private fun isValid(o: JsonPrimitive?) = o?.isString == true && o.content.isNotEmpty()

class JsonAuthProvider(private val config: JsonAuthConfig) : AuthenticationProvider(config) {
  override suspend fun onAuthenticate(context: AuthenticationContext) {
    val json = context.call.receive<JsonObject>()
    val username = json.get("username")?.jsonPrimitive
    val password = json.get("password")?.jsonPrimitive

    var credentials: UserPasswordCredential? = null
    if (isValid(username) && isValid(password)) {
      credentials = UserPasswordCredential(username!!.content, password!!.content)
      val authenticatedUser = config.validationLambda?.invoke(JsonAuthValidationContext(context.call), credentials)
      authenticatedUser?.also {
        context.principal(it)
        return
      }
    }

    config.challengeLambda?.invoke(JsonAuthChallengeContext(context.call), credentials)
  }
}
