package studio.pinkcloud.module.authentication.refactor

interface IAuthRepository<T : IAgentSession> {
  suspend fun authorizeAgent(
    agentName: String,
    pwdHash: String,
  ): T?

  suspend fun saveSession(session: T): T

  suspend fun validateSession(session: T): Boolean

  suspend fun invalidateSession(session: T)

  suspend fun invalidateAllSessions(agentName: String)
}
