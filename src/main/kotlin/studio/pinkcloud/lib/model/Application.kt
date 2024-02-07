package studio.pinkcloud.lib.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.util.Date

data class Application(
  @BsonId
  val id: ObjectId,
  val name: String,
  val apiKey: String,
  val lastSeenAt: Date? = null,
)
