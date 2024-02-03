package studio.pinkcloud.lib.type

import kotlinx.serialization.Serializable
import studio.pinkcloud.module.agentauth.lib.type.IAgentAuthSession
import studio.pinkcloud.module.directauth.lib.type.IDirectAgentSession

@Serializable
data class AgentSession(
  override val agentName: String,
  override val agentEmail: String,
  override val sessionId: String,
) : IDirectAgentSession, IAgentAuthSession
