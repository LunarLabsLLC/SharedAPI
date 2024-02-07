package studio.pinkcloud.config

import kotlinx.serialization.Serializable
import studio.pinkcloud.module.directauth.config.CookieSecretsConfig
import studio.pinkcloud.module.directauth.config.IDirectAuthConfig

@Serializable
data class APIConfig(
  override var developmentEnv: String = "development",
  override var cookieSecrets: CookieSecretsConfig = CookieSecretsConfig(),
  var database: DatabaseConfig = DatabaseConfig(),
  var security: SecurityConfig = SecurityConfig(),
  var email: EmailConfig = EmailConfig(),
  var discordClient: DiscordOAuthConfig = DiscordOAuthConfig(),
) : IDirectAuthConfig

@Serializable
data class DatabaseConfig(
  var name: String = "PCSSharedAPI",
  var connectionStr: String = "",
)

@Serializable
data class DiscordOAuthConfig(
  var id: String = "",
  var secret: String = "",
)

@Serializable
data class SecurityConfig(
  var logRounds: Int = 12,
)

@Serializable
data class EmailConfig(
  var address: String = "no-reply@pinkcloud.studio",
  var password: String = "",
)
