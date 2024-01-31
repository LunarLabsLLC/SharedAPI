package studio.pinkcloud.module.authentication.lib

import io.ktor.server.auth.Principal

abstract class IAgentSession(open val agentName: String, open val sessionId: String) : Principal
