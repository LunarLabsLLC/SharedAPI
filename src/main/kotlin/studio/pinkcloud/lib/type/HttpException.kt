package studio.pinkcloud.lib.type

import io.ktor.http.HttpStatusCode

enum class HttpError(val statusCode: HttpStatusCode, val message: String) {
  AlreadyLoggedIn(HttpStatusCode.Forbidden, "Already logged in."),
  BadRequest(HttpStatusCode.BadRequest, "Invalid request."),
  EmailConflict(HttpStatusCode.Conflict, "Email already registered."),
  InvalidCredentials(HttpStatusCode.Unauthorized, "Invalid credentials."),
  InvalidSession(HttpStatusCode.Unauthorized, "Invalid session."),
  UsernameConflict(HttpStatusCode.Conflict, "Username already registered."),
}

fun HttpError.get(): HttpException {
  return HttpException(statusCode, message)
}

open class HttpException(
  val statusCode: HttpStatusCode,
  override val message: String? = null,
  override val cause: Throwable? = null,
) :
  Exception(message, cause) {
  override fun toString(): String {
    return "${super.toString()}.\n" +
      "Caused by: ${super.cause}" + super.cause?.stackTraceToString()
  }
}
