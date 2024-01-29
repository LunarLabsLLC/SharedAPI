package studio.pinkcloud.module.authentication.lib

import io.ktor.server.auth.*

open class IUserSession(open val username: String, open val id: String) : Principal