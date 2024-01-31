package studio.pinkcloud.business.repository

import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import kotlinx.coroutines.flow.firstOrNull
import org.bson.types.ObjectId
import studio.pinkcloud.business.AppDbContext
import studio.pinkcloud.lib.model.Agent
import studio.pinkcloud.lib.model.Session
import studio.pinkcloud.lib.type.AgentSession
import studio.pinkcloud.module.authentication.refactor.IAuthRepository

object AuthRepository : IAuthRepository<AgentSession> {
  private suspend fun getAgentFromName(agentName: String): Agent? {
    return AppDbContext.agents.find<Agent>(Filters.eq(Agent::name.name, agentName)).firstOrNull()
  }

  suspend fun registerAgent(
    agentName: String,
    agentEmail: String,
    pwdHash: String,
  ): Agent {
    val agent = Agent(ObjectId(), agentName, agentEmail, pwdHash, mutableSetOf())
    AppDbContext.agents.insertOne(agent)
    return agent
  }

  override suspend fun authorizeAgent(
    agentName: String,
    pwdHash: String,
  ): AgentSession? {
    val agent = getAgentFromName(agentName)
    return if (agent != null && agent.pwdHash == pwdHash) {
      AgentSession(agentName, ObjectId.get())
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
          Session(session.sessionId),
        ),
        Updates.currentDate(Agent::lastSessionAt.name),
      )
    val options = UpdateOptions().upsert(true)
    AppDbContext.agents.updateOne(query, params, options)
    return session
  }

  override suspend fun validateSession(session: AgentSession): Boolean {
    return getAgentFromName(session.agentName)?.sessions?.any { s -> s.id == session.sessionId } ?: false
  }

  override suspend fun invalidateSession(session: AgentSession) {
    val query = Filters.eq(Agent::name.name, session.agentName)
    val params =
      Updates.combine(
        Updates.pull(Agent::sessions.name, Session(session.sessionId)),
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
