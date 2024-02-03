package studio.pinkcloud.config

import kotlinx.serialization.Serializable
import studio.pinkcloud.module.directauth.config.CookieSecretsConfig
import studio.pinkcloud.module.directauth.config.IDirectAuthConfig

@Serializable
data class APIConfig(
  var database: DatabaseConfig = DatabaseConfig(),
  var discordClient: DiscordOAuthConfig = DiscordOAuthConfig(),
  var security: SecurityConfig = SecurityConfig(),
  override var developmentEnv: String = "development",
  override var cookieSecrets: CookieSecretsConfig = CookieSecretsConfig(),
) : IDirectAuthConfig

@Serializable
data class DatabaseConfig(
  var name: String = "PinkCloud",
  var connectionStr: String = "mongodb://localhost:27017",
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
