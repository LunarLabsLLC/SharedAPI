package studio.pinkcloud.business.repository

import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import kotlinx.coroutines.flow.firstOrNull
import org.bson.types.ObjectId
import studio.pinkcloud.business.AppDbContext
import studio.pinkcloud.helpers.checkPwdHash
import studio.pinkcloud.helpers.getPwdHash
import studio.pinkcloud.lib.model.Agent
import studio.pinkcloud.lib.model.Session
import studio.pinkcloud.lib.type.AgentSession
import studio.pinkcloud.lib.type.HttpError
import studio.pinkcloud.lib.type.get
import studio.pinkcloud.module.directauth.business.repository.IDirectAuthRepository

object AuthRepository : IDirectAuthRepository<AgentSession> {
  private suspend fun getAgentFromName(agentName: String): Agent? {
    return AppDbContext.agents.find<Agent>(Filters.eq(Agent::name.name, agentName)).firstOrNull()
  }

  private suspend fun getAgentFromEmail(email: String): Agent? {
    return AppDbContext.agents.find<Agent>(Filters.eq(Agent::email.name, email)).firstOrNull()
  }

  override suspend fun registerAgent(
    agentName: String,
    agentEmail: String,
    password: String,
  ): AgentSession {
    if (getAgentFromName(agentName) != null) throw HttpError.UsernameConflict.get()
    if (getAgentFromEmail(agentEmail) != null) throw HttpError.EmailConflict.get()
    val agent = Agent(ObjectId(), agentName, agentEmail, getPwdHash(password), mutableSetOf())
    AppDbContext.agents.insertOne(agent)
    return AgentSession(agentName, ObjectId.get().toString())
  }

  override suspend fun authorizeAgent(
    agentName: String,
    password: String,
  ): AgentSession? {
    val agent = getAgentFromName(agentName)
    return if (agent != null && checkPwdHash(password, agent.pwdHash)) {
      AgentSession(agentName, ObjectId.get().toString())
    } else {
      null
    }
  }

  override suspend fun saveSession(session: AgentSession): AgentSession {
    val query = Filters.eq(Agent::name.name, session.agentName)
    val params =
      Updates.combine(
        Updates.addToSet(
          Agent::sessions.name,
          Session(ObjectId(session.sessionId)),
        ),
        Updates.currentDate(Agent::lastSessionAt.name),
      )
    val options = UpdateOptions().upsert(true)
    AppDbContext.agents.updateOne(query, params, options)
    return session
  }

  override suspend fun validateSession(session: AgentSession): Boolean {
    return getAgentFromName(session.agentName)?.sessions
      ?.any { s -> s.id.toString() == session.sessionId } ?: false
  }

  override suspend fun invalidateSession(session: AgentSession) {
    val query = Filters.eq(Agent::name.name, session.agentName)
    val params =
      Updates.combine(
        Updates.pull(Agent::sessions.name, Session(ObjectId(session.sessionId))),
        Updates.currentDate(Agent::lastSessionAt.name),
      )
    val options = UpdateOptions().upsert(true)
    AppDbContext.agents.updateOne(query, params, options)
  }

  override suspend fun invalidateAllSessions(agentName: String) {
    val query = Filters.eq(Agent::name.name, agentName)
    val params =
      Updates.combine(
        Updates.set(Agent::sessions.name, mutableSetOf<Session>()),
        Updates.currentDate(Agent::lastSessionAt.name),
      )
    val options = UpdateOptions().upsert(true)
    AppDbContext.agents.updateOne(query, params, options)
  }
}
