package studio.pinkcloud.business.service

import org.bson.types.ObjectId
import studio.pinkcloud.business.repository.AuthRepository
import studio.pinkcloud.helpers.getPwdHash
import studio.pinkcloud.lib.type.AgentSession

object AuthService {
  private var authRepository: AuthRepository = AuthRepository

  suspend fun authorizeAgent(
    agentName: String,
    password: String,
  ): AgentSession? {
    val session = authRepository.authorizeAgent(agentName, getPwdHash(password))
    if (session != null) {
      authRepository.saveSession(session)
    }
    return session
  }

  suspend fun registerAgent(
    agentName: String,
    agentEmail: String,
    password: String,
  ): AgentSession {
    authRepository.registerAgent(agentName, agentEmail, getPwdHash(password))
    val session = AgentSession(agentName, ObjectId.get())
    authRepository.saveSession(session)
    return session
  }

  suspend fun validateSession(session: AgentSession): Boolean = authRepository.validateSession(session)

  suspend fun invalidateSession(session: AgentSession) = authRepository.invalidateSession(session)

  suspend fun invalidateAll(agentName: String) = authRepository.invalidateAllSessions(agentName)
}
