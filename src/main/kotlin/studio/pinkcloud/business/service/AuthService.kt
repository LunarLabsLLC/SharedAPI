package studio.pinkcloud.business.service

import org.bson.types.ObjectId
import studio.pinkcloud.business.repository.AuthRepository
import studio.pinkcloud.lib.type.AgentSession

object AuthService {
  private var authRepository: AuthRepository = AuthRepository

  suspend fun login(
    agentName: String,
    password: String,
  ): AgentSession? {
    return authRepository.authorizeAgent(agentName, password)
      ?.also { authRepository.saveSession(it) }
  }

  suspend fun register(
    agentName: String,
    agentEmail: String,
    password: String,
  ): AgentSession {
    authRepository.registerAgent(agentName, agentEmail, password)
    return AgentSession(agentName, ObjectId.get().toString())
  }

  suspend fun saveSession(session: AgentSession): AgentSession = authRepository.saveSession(session)

  suspend fun validateSession(session: AgentSession): Boolean = authRepository.validateSession(session)

  suspend fun invalidateSession(session: AgentSession) = authRepository.invalidateSession(session)

  suspend fun invalidateAll(agentName: String) = authRepository.invalidateAllSessions(agentName)
}
