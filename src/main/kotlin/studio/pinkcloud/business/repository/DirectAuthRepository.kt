package studio.pinkcloud.business.repository

import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import org.bson.types.ObjectId
import studio.pinkcloud.business.AppDbContext
import studio.pinkcloud.business.repository.BaseAuthRepository.getAgentFromEmail
import studio.pinkcloud.helpers.checkPwdHash
import studio.pinkcloud.helpers.getPwdHash
import studio.pinkcloud.lib.model.Agent
import studio.pinkcloud.lib.model.Session
import studio.pinkcloud.lib.type.AgentSession
import studio.pinkcloud.lib.type.HttpError
import studio.pinkcloud.lib.type.get
import studio.pinkcloud.module.directauth.business.repository.IDirectAuthRepository

object DirectAuthRepository : IDirectAuthRepository<AgentSession> {
  override suspend fun registerAgent(
    agentName: String,
    agentEmail: String,
    password: String,
  ): AgentSession {
    if (BaseAuthRepository.getAgentFromName(agentName) != null) throw HttpError.UsernameConflict.get()
    if (getAgentFromEmail(agentEmail) != null) throw HttpError.EmailConflict.get()
    val agent = Agent(ObjectId(), agentEmail, mutableSetOf(), agentName, getPwdHash(password))
    AppDbContext.agents.insertOne(agent)
    return AgentSession(agentName, agentEmail, ObjectId.get().toString())
  }

  override suspend fun authorizeAgent(
    agentName: String,
    password: String,
  ): AgentSession? {
    val agent = BaseAuthRepository.getAgentFromName(agentName)
    return if (agent != null && checkPwdHash(password, agent.pwdHash)) {
      AgentSession(agentName, agent.email, ObjectId.get().toString())
    } else {
      null
    }
  }

  override suspend fun validateSession(session: AgentSession): Boolean {
    return BaseAuthRepository.getAgentFromName(session.agentName)?.sessions
      ?.any { s -> s.id.toString() == session.sessionId } ?: false
  }

  override suspend fun invalidateSession(session: AgentSession) {
    val query = Filters.eq(Agent::name.name, session.agentName)
    val params =
      Updates.combine(
        Updates.pull(Agent::sessions.name, Session(ObjectId(session.sessionId))),
        Updates.currentDate(Agent::lastSeenAt.name),
      )
    val options = UpdateOptions()
    AppDbContext.agents.updateOne(query, params, options)
  }

  override suspend fun saveSession(session: AgentSession): AgentSession = BaseAuthRepository.saveSession(session)

  override suspend fun invalidateAllSessions(agentName: String) = BaseAuthRepository.invalidateAllSessions(agentName)
}
