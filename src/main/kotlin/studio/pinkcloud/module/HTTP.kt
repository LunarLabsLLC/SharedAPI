package studio.pinkcloud.module

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.forwardedheaders.*

fun Application.configureHTTP() {
    install(AutoHeadResponse)

    /// WARNING: for security, do not include these if not behind a reverse proxy
    install(ForwardedHeaders)
    install(XForwardedHeaders)
    ///

    install(DefaultHeaders) {
        header("Server", "Omitted")
    }

    install(CORS) {
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Head)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        // allowHeader("MyCustomHeader")
        // exposeHeader("MyCustomHeader")
        // allowHeadersPrefixed("custom-")
        // allowCredentials = true
        // allowHost("client-host:8081", subDomains = listOf("en", "de", "es"))
    }
}
