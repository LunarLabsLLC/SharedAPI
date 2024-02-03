package studio.pinkcloud.module.directauth.lib.type

import io.ktor.server.auth.Principal

interface IDirectAgentSession : Principal {
  val agentName: String
  val sessionId: String
}
