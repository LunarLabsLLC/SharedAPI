package studio.pinkcloud.module.directauth.business.repository

import studio.pinkcloud.module.directauth.lib.type.IDirectAgentSession

interface IDirectAuthRepository<T : IDirectAgentSession> {
//  suspend fun registerAgent(
//    agentName: String,
//    agentEmail: String,
//    password: String,
//  ): T

  suspend fun authorizeAgent(
    agentName: String,
    password: String,
  ): T?

  suspend fun saveSession(session: T): T

  suspend fun validateSession(session: T): Boolean

  suspend fun invalidateSession(session: T)

  suspend fun invalidateAllSessions(agentName: String)
}
