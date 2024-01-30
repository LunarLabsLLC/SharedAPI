package studio.pinkcloud.config

import com.akuleshov7.ktoml.Toml
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import java.io.File
import kotlin.reflect.full.memberProperties

fun File.toConfig(): APIConfig {
  var configStr: String? = null
  if (!exists()) {
    createNewFile()
    configStr =
      Toml.encodeToString(APIConfig())
        .replace(Regex("developmentEnv(.*)\n"), "")
    writeText(configStr)
  } else {
    // TODO: Update current file
  }
  configStr = configStr ?: readText(Charsets.UTF_8)
  return Toml.decodeFromString<APIConfig>(configStr)
}

fun loadConfig() {
  val envConfigs =
    mapOf(
      "development" to "-dev",
      "production" to "-prod",
    )

  val env = System.getenv("DEPLOYMENT_ENV")
  API_CONFIG =
    mutableSetOf("")
      .apply { envConfigs[env]?.let { add(it) } }
      .apply { add("-local") }
      .map { File("config$it.toml").toConfig() }
      .mergeAll()
  API_CONFIG.developmentEnv = env

  APIConfig::class.memberProperties.forEach {
    if (it.get(API_CONFIG)?.equals("") == true) {
      throw Exception("${it.name} config not set")
    }
  }
}

private fun <T> Iterable<T>.mergeAll(): T {
  // reduce { acc, c -> acc + c }
  return elementAt(1) // TODO: Find a way to merge TOML trees in priority
}

lateinit var API_CONFIG: APIConfig
