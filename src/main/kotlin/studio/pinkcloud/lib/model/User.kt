package studio.pinkcloud.lib.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
  @BsonId
  val id: ObjectId,
  val sessions: List<Session>,
)

data class Session(
  @BsonId
  val id: ObjectId,
)
