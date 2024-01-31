package studio.pinkcloud.config

import com.akuleshov7.ktoml.Toml
import com.akuleshov7.ktoml.TomlInputConfig
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties

val tomlDecoderConfig =
  TomlInputConfig(
    allowEscapedQuotesInLiteralStrings = true,
    ignoreUnknownNames = true,
    allowEmptyValues = true,
    allowNullValues = true,
    allowEmptyToml = true,
  )

fun File.toConfig(): APIConfig {
  val configStr: String?
  if (!exists()) {
    createNewFile()
    configStr =
      Toml.encodeToString(APIConfig())
        .replace(Regex("developmentEnv(.*)\n"), "")
    writeText(configStr)
  } else {
    configStr = readText(Charsets.UTF_8)
    updateFile(configStr)
  }
  return Toml(tomlDecoderConfig).decodeFromString<APIConfig>(configStr)
}

// Decodes, reencodes and then rewrites to add any new fields that might be missing in the file
// Runs in a separate thread
@OptIn(DelicateCoroutinesApi::class)
fun File.updateFile(configStr: String) =
  GlobalScope.async {
    configStr
      .let { Toml(tomlDecoderConfig).decodeFromString<APIConfig>(it) }
      .let {
        Toml.encodeToString(it)
          .replace(Regex("developmentEnv(.*)\n"), "")
      }
      .let { writeText(it) }
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
  return reduce { acc, next ->
    recursiveSetAll(acc, next)
    acc
  }
}

private fun <T> recursiveSetAll(
  instance1: T,
  instance2: T,
) {
  instance1!!::class.memberProperties.forEach {
    val value = it.getter.call(instance2)
    val klass = it.returnType.classifier as? KClass<*>
    if (klass != null && (
        klass.annotations.any { it is Serializable } || klass.isSubclassOf(Serializable::class)
      )
    ) {
      recursiveSetAll(it.getter.call(instance1), value)
    } else {
      (it as? KMutableProperty1<out T & Any, *>)?.let {
        if (value != null && value != getDefaultValue(String::class)) {
          try {
            it.setter.call(instance1, value)
          } catch (_: Exception) {
          }
        }
      }
    }
  }
}

fun <T : Any> getDefaultValue(type: KClass<T>): T? {
  @Suppress("UNCHECKED_CAST")
  return when (type) {
    String::class -> ""
    Int::class -> 0
    Boolean::class -> false
    else -> null
  } as T?
}

lateinit var API_CONFIG: APIConfig
