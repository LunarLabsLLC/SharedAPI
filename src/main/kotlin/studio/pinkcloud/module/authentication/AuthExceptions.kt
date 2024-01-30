package studio.pinkcloud.module.authentication

import io.ktor.http.*

class AuthenticationException() : CustomHttpException(HttpStatusCode.Unauthorized, "Invalid credentials.")

class AuthorizationException() : CustomHttpException(HttpStatusCode.Unauthorized, "Invalid session.")

open class CustomHttpException(
  val StatusCode: HttpStatusCode,
  override val message: String? = null,
  override val cause: Throwable? = null,
) :
  Exception(message, cause)
