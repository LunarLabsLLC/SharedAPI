package studio.pinkcloud.business.repository

import studio.pinkcloud.module.authentication.lib.IAuthRepository
import java.util.*

object AuthRepository: IAuthRepository { // TODO: Implement
    override suspend fun validateSession(username: String, sessionId: String): Boolean  {
        return true
    }

    override suspend fun invalidateSession(username: String, sessionId: String) {
        // Remove session from DB
    }

    override suspend fun invalidateAllSessions(username: String) {
        // Remove all sessions from DB
    }

    override suspend fun authorizeAgent(username: String, password: String): UUID? {
        // Validate credentials
        if (false) return null

        val uuid = UUID.randomUUID()
        saveSession(username, sessionId = uuid.toString())
        return uuid
    }
    private suspend fun saveSession(username: String, sessionId: String) {

    }
}