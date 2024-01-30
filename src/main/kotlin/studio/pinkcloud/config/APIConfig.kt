package studio.pinkcloud.config

import kotlinx.serialization.Serializable

@Serializable
data class APIConfig(
    var developmentEnv: String = "development",
    var database: DatabaseConfig = DatabaseConfig(),
    var cookieSecrets: CookieSecretsConfig = CookieSecretsConfig(),
    var discordClient: DiscordOAuthConfig = DiscordOAuthConfig()
)

@Serializable
data class DatabaseConfig(
    var name: String = "PinkCloud",
    var connectionStr: String = "mongodb://localhost:27017",
)

@Serializable
data class CookieSecretsConfig(
    var sign: String = "",
    var encryption: String = "",
)

@Serializable
data class DiscordOAuthConfig(
    var id: String = "",
    var secret: String = "",
)