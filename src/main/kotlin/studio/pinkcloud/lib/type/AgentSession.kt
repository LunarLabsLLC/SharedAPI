package studio.pinkcloud.lib.type

import studio.pinkcloud.module.directauth.lib.type.IAgentSession

data class AgentSession(
  override val agentName: String,
  override val sessionId: String,
) : IAgentSession
