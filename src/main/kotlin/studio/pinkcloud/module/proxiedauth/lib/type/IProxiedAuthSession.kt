package studio.pinkcloud.module.proxiedauth.lib.type

import io.ktor.server.auth.Principal

interface IProxiedAuthSession : Principal {
  val agentName: String
  val agentEmail: String
  val sessionId: String
}
