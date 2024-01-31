package studio.pinkcloud.lib.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.util.Date

data class Agent(
  @BsonId
  val id: ObjectId,
  val name: String,
  val email: String,
  val pwdHash: String,
  val sessions: MutableSet<Session>,
  val lastSessionAt: Date? = null,
)

data class Session(
  @BsonId
  val id: ObjectId,
)
