package studio.pinkcloud.module.authentication

import studio.pinkcloud.module.authentication.lib.IAuthRepository
import studio.pinkcloud.module.authentication.lib.IUserSession

class SessionManager private constructor(private val authRepository: IAuthRepository) {
    companion object {
        private lateinit var instance: SessionManager

        fun get(): SessionManager {
            return instance
        }

        fun init(authRepository: IAuthRepository) {
            instance = SessionManager(authRepository)
        }
    }

    suspend fun <T: IUserSession>authorize(
        username: String,
        password: String,
        sessionGenerator: (username: String, password: String) -> T
    ): T? {
        val uuid = authRepository.authorizeAgent(username, password)
        return uuid?.let {
            sessionGenerator(username, uuid.toString())
        }
    }

    suspend fun validate(session: IUserSession): Boolean =
        authRepository.validateSession(session.username, session.id)

    suspend fun invalidate(session: IUserSession) =
        authRepository.invalidateSession(session.username, session.id)

    suspend fun invalidateAll(username: String) =
        authRepository.invalidateAllSessions(username)
}