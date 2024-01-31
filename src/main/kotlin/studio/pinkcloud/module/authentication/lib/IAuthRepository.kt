package studio.pinkcloud.module.authentication.lib

import studio.pinkcloud.lib.model.Agent

interface IAuthRepository<T : IAgentSession> {
  suspend fun registerAgent(
    agentName: String,
    agentEmail: String,
    password: String,
  ): Agent

  suspend fun authorizeAgent(
    agentName: String,
    password: String,
  ): T?

  suspend fun saveSession(session: T): T

  suspend fun validateSession(session: T): Boolean

  suspend fun invalidateSession(session: T)

  suspend fun invalidateAllSessions(agentName: String)
}
