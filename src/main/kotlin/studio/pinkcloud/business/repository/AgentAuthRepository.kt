package studio.pinkcloud.business.repository

import com.mongodb.client.model.Filters
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
import studio.pinkcloud.module.agentauth.business.repository.IAgentAuthRepository
import java.util.UUID

object AgentAuthRepository : IAgentAuthRepository<AgentSession> {
  override suspend fun createAgent(email: String): String {
    if (BaseAuthRepository.getAgentFromEmail(email) != null) throw HttpError.EmailConflict.get()

    val uuid = UUID.randomUUID().toString()
    AppDbContext.agents.insertOne(
      Agent(ObjectId.get(), email, token = uuid),
    )
    return uuid
  }

  override suspend fun registerAgent(
    token: String,
    agentName: String,
    password: String,
  ): AgentSession? {
    if (BaseAuthRepository.getAgentFromName(agentName) != null) throw HttpError.UsernameConflict.get()
    val query = Filters.eq(Agent::token.name, token)
    val params =
      Updates.combine(
        Updates.set(
          Agent::name.name,
          agentName,
        ),
        Updates.set(
          Agent::pwdHash.name,
          getPwdHash(password),
        ),
        Updates.set(
          Agent::token.name,
          null,
        ),
        Updates.currentDate(Agent::lastSessionAt.name),
      )
    AppDbContext.agents.updateOne(query, params)
      .also {
        if (it.modifiedCount == 0L) {
          return null
        }
      }

    BaseAuthRepository.getAgentFromName(agentName)
      ?.also {
        return AgentSession(agentName, it.email, ObjectId.get().toString())
      }
    return null
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

  override suspend fun invalidateSession(sessionId: String) {
    val query = Filters.elemMatch(Agent::sessions.name, Filters.eq("_id", ObjectId(sessionId)))
    val meTest = AppDbContext.agents.find<Agent>(query).firstOrNull()
    println(meTest)
    val params =
      Updates.combine(
        Updates.pull(Agent::sessions.name, Session(ObjectId(sessionId))),
        Updates.currentDate(Agent::lastSessionAt.name),
      )
    AppDbContext.agents.updateOne(query, params)
  }

  override suspend fun saveSession(session: AgentSession): AgentSession = BaseAuthRepository.saveSession(session)

  override suspend fun invalidateAllSessions(agentName: String) = BaseAuthRepository.invalidateAllSessions(agentName)
}
