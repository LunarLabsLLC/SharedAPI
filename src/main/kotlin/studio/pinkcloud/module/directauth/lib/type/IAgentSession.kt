package studio.pinkcloud.module.directauth.lib.type

import io.ktor.server.auth.Principal

interface IAgentSession : Principal {
  val agentName: String
  val sessionId: String
}
