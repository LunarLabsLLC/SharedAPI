package studio.pinkcloud.lib.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.util.Date

data class Agent(
  @BsonId
  val id: ObjectId,
  val email: String,
  val sessions: MutableSet<Session> = mutableSetOf(),
  val name: String? = null,
  val pwdHash: String? = null,
  val token: String? = null,
  val lastSessionAt: Date? = null,
)
