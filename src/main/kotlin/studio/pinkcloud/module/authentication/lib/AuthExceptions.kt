package studio.pinkcloud.module.authentication.lib

import io.ktor.http.HttpStatusCode

class AuthenticationException() : CustomHttpException(HttpStatusCode.Unauthorized, "Invalid credentials.")

class AuthorizationException() : CustomHttpException(HttpStatusCode.Unauthorized, "Invalid session.")

open class CustomHttpException(
  val StatusCode: HttpStatusCode,
  override val message: String? = null,
  override val cause: Throwable? = null,
) :
  Exception(message, cause) {
  override fun toString(): String {
    return "${super.toString()}.\n" +
      "Caused by: ${super.cause}" + super.cause?.stackTraceToString()
  }
}
