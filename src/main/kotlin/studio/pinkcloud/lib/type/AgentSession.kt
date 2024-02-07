package studio.pinkcloud.lib.type

import kotlinx.serialization.Serializable
import studio.pinkcloud.module.directauth.lib.type.IDirectAgentSession
import studio.pinkcloud.module.proxiedauth.lib.type.IProxiedAuthSession

@Serializable
data class AgentSession(
  override val agentName: String,
  override val agentEmail: String,
  override val sessionId: String,
) : IDirectAgentSession, IProxiedAuthSession
