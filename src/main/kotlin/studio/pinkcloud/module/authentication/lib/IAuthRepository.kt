package studio.pinkcloud.module.authentication.lib

import java.util.*

interface IAuthRepository {
    suspend fun authorizeAgent(username: String, password: String): UUID?
    suspend fun validateSession(username: String, sessionId: String): Boolean
    suspend fun invalidateSession(username: String, sessionId: String)
    suspend fun invalidateAllSessions(username: String)
}