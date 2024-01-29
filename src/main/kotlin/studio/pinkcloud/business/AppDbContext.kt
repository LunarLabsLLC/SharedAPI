package studio.pinkcloud.business

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import studio.pinkcloud.config.API_CONFIG

object AppDbContext {
    lateinit var Database: MongoDatabase;
    fun connect() {
        // Create a MongoDB client and database instance
        val clientSettings = MongoClientSettings.builder()
            .applyConnectionString(ConnectionString(API_CONFIG.dbConnectionStr))
            .build()
        val client = MongoClient.create(clientSettings)
        Database = client.getDatabase(API_CONFIG.dbName)
    }
}
