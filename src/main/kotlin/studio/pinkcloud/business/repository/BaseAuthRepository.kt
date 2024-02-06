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

object BaseAuthRepository {
  suspend fun getAgentFromName(agentName: String): Agent? {
    return AppDbContext.agents.find<Agent>(Filters.eq(Agent::name.name, agentName)).firstOrNull()
  }

  suspend fun getAgentFromEmail(email: String): Agent? {
    return AppDbContext.agents.find<Agent>(Filters.eq(Agent::email.name, email)).firstOrNull()
  }

  suspend fun saveSession(session: AgentSession): AgentSession {
    val query = Filters.eq(Agent::name.name, session.agentName)
    val params =
      Updates.combine(
        Updates.addToSet(
          Agent::sessions.name,
          Session(ObjectId(session.sessionId)),
        ),
        Updates.currentDate(Agent::lastSessionAt.name),
      )
    val options = UpdateOptions()
    AppDbContext.agents.updateOne(query, params, options)
    return session
  }

  suspend fun invalidateAllSessions(agentName: String) {
    val query = Filters.eq(Agent::name.name, agentName)
    val params =
      Updates.combine(
        Updates.set(Agent::sessions.name, mutableSetOf<Session>()),
        Updates.currentDate(Agent::lastSessionAt.name),
      )
    val options = UpdateOptions()
    AppDbContext.agents.updateOne(query, params, options)
  }
}
