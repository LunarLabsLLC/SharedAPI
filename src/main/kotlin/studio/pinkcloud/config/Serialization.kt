package studio.pinkcloud.config

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

fun Application.configureSerialization() {
  install(ContentNegotiation) {
    json(
      contentType = ContentType.Application.Json,
      json =
        kotlinx.serialization.json.Json {
          prettyPrint = true
        },
    )
  }
}
