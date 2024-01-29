package studio.pinkcloud.module.authentication

import io.ktor.server.auth.*
import studio.pinkcloud.module.authentication.lib.IAuthRepository
import studio.pinkcloud.module.authentication.lib.IUserSession

class SessionManager<T: IUserSession> private constructor(private val authRepository: IAuthRepository) {
    companion object {
        private lateinit var instance: SessionManager<*>

        @Suppress("UNCHECKED_CAST")
        fun <T : IUserSession> get(): SessionManager<T> {
            return instance as SessionManager<T>
        }

        fun <T : IUserSession> init(authRepository: IAuthRepository) {
            instance = SessionManager<T>(authRepository)
        }
    }

    suspend fun authorize(
        username: String,
        password: String,
        sessionGenerator: (username: String, password: String) -> IUserSession
    ): IUserSession? {
        val uuid = authRepository.authorizeAgent(username, password)
        return uuid?.let { sessionGenerator(username, uuid.toString()) }
    }

    suspend fun validate(session: IUserSession): Boolean =
        authRepository.validateSession(session.username, session.id)

    suspend fun invalidate(session: IUserSession) =
        authRepository.invalidateSession(session.username, session.id)

    suspend fun invalidateAll(username: String) =
        authRepository.invalidateAllSessions(username)
}