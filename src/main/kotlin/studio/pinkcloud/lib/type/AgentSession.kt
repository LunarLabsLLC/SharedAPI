package studio.pinkcloud.lib.type

import org.bson.types.ObjectId
import studio.pinkcloud.module.authentication.refactor.IAgentSession

data class AgentSession(
  override val agentName: String,
  override val sessionId: ObjectId,
) :
  IAgentSession(agentName, sessionId)
