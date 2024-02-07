package studio.pinkcloud.module.proxiedauth.business.repository

import studio.pinkcloud.module.proxiedauth.lib.type.IProxiedAuthSession

interface IProxiedAuthRepository<T : IProxiedAuthSession> {
  suspend fun createAgent(email: String): String

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
