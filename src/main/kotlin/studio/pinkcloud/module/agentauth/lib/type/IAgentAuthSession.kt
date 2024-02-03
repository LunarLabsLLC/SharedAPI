package studio.pinkcloud.module.agentauth.lib.type

import io.ktor.server.auth.Principal

interface IAgentAuthSession : Principal {
  val agentName: String
  val agentEmail: String
  val sessionId: String
}
