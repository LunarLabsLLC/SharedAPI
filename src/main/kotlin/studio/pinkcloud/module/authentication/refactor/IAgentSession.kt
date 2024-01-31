package studio.pinkcloud.module.authentication.refactor

import io.ktor.server.auth.Principal
import org.bson.types.ObjectId

abstract class IAgentSession(open val agentName: String, open val sessionId: ObjectId) : Principal
