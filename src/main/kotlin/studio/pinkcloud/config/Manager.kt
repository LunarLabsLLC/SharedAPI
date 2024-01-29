package studio.pinkcloud.config

import java.io.File
import com.akuleshov7.ktoml.Toml
import kotlinx.serialization.encodeToString
import kotlin.reflect.full.memberProperties

fun loadConfig() {
    val env = System.getenv("DEPLOYMENT_ENV");
    val fileName = if (env == "development") {
        "config-dev.yml"
    } else {
        "config.yml"
    }

    File(fileName).apply {
        if (!exists()) {
            createNewFile()
            writeText(
                Toml.encodeToString(APIConfig()).replace(Regex("developmentEnv: (.+)"), "")
            )
            throw Exception("Config file not found. A new template has been generated.")
        } else {
            API_CONFIG = Toml.decodeFromString(APIConfig.serializer(), readText(Charsets.UTF_8))
            API_CONFIG.developmentEnv = env
        }
    }

    for (prop in APIConfig::class.memberProperties) {
        if (prop.get(API_CONFIG)?.equals("") == true) throw Exception("${prop.name} config not set")
    }
}

lateinit var API_CONFIG: APIConfig