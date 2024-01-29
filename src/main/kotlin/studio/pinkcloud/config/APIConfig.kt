package studio.pinkcloud.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class APIConfig(
    var developmentEnv: String = "development",

    val dbName: String = "",
    val dbConnectionStr: String = "",

    val cookieSecretEncKey: String = "",
    val cookieSecretSignKey: String = "",

    val discordClientId: String = "",
    val discordClientSecret: String = "",
)