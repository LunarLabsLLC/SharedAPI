package studio.pinkcloud.lib.type

import studio.pinkcloud.module.directauth.lib.type.IDirectAgentSession

data class AgentSession(
  override val agentName: String,
  override val sessionId: String,
) : IDirectAgentSession
