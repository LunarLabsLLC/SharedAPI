package studio.pinkcloud.module.agentauth.business.repository

import studio.pinkcloud.module.agentauth.lib.type.IAgentAuthSession

interface IAgentAuthRepository<T : IAgentAuthSession> {
  suspend fun registerAgent(
    token: String,
    agentName: String,
    password: String,
  ): T?

  suspend fun authorizeAgent(
    agentName: String,
    password: String,
  ): T?

  suspend fun saveSession(session: T): T

  suspend fun invalidateSession(sessionId: String)

  suspend fun invalidateAllSessions(agentName: String)
}
