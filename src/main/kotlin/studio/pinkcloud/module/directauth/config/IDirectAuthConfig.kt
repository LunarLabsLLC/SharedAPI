package studio.pinkcloud.module.directauth.config

import kotlinx.serialization.Serializable

interface IDirectAuthConfig {
  var developmentEnv: String
  var cookieSecrets: CookieSecretsConfig
}

@Serializable
open class CookieSecretsConfig(
  open var sign: String = "",
  open var encryption: String = "",
)
