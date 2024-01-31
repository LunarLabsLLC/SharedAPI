package studio.pinkcloud.business

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.runBlocking
import org.bson.BsonInt64
import org.bson.Document
import studio.pinkcloud.config.API_CONFIG
import studio.pinkcloud.lib.model.Agent

object AppDbContext {
  private var database: MongoDatabase
  var agents: MongoCollection<Agent>
    private set

  init {
    runBlocking { database = connect() }
    agents = database.getCollection<Agent>("Agents")
  }

  private suspend fun connect(): MongoDatabase {
    val connStr = ConnectionString(API_CONFIG.database.connectionStr)
    val client = MongoClientSettings.builder().applyConnectionString(connStr).build()
    return MongoClient.create(client)
      .getDatabase(API_CONFIG.database.name)
      .apply { runCommand(Document("ping", BsonInt64(1))) }
  }
}
