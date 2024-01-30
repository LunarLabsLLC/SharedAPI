package studio.pinkcloud.business

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.runBlocking
import org.bson.BsonInt64
import org.bson.Document
import studio.pinkcloud.config.API_CONFIG

object AppDbContext {
  lateinit var database: MongoDatabase

  fun connect(): MongoDatabase {
    database =
      runBlocking {
        val connStr = ConnectionString(API_CONFIG.database.connectionStr)
        val client = MongoClientSettings.builder().applyConnectionString(connStr).build()
        MongoClient.create(client)
          .getDatabase(API_CONFIG.database.name)
          .apply { runCommand(Document("ping", BsonInt64(1))) }
      }
    return database
  }
}
