package studio.pinkcloud.lib.type

import studio.pinkcloud.module.authentication.lib.IAgentSession

data class AgentSession(
  override val agentName: String,
  override val sessionId: String,
) :
  IAgentSession(agentName, sessionId)
