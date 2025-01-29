package de.royzer.fabrichg.mongodb

import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.toList
import net.fabricmc.loader.api.FabricLoader
import net.silkmc.silk.core.logging.logger
import org.bson.UuidRepresentation
import java.util.concurrent.TimeUnit

object MongoManager {
    lateinit var client: MongoClient
    lateinit var database: MongoDatabase
    var isConnected: Boolean = false

    suspend fun connect(): MongoDatabase {
        client = if (FabricLoader.getInstance().isDevelopmentEnvironment) {
            MongoClient.create(
                MongoClientSettings.builder()
                    .timeout(2, TimeUnit.SECONDS)
                    .uuidRepresentation(UuidRepresentation.STANDARD)
                    .build()
            )
        } else {
            MongoClient.create(
                MongoClientSettings.builder()
                    .uuidRepresentation(UuidRepresentation.STANDARD)
                    .credential(
                        MongoCredential.createCredential(
                            System.getenv("MONGODB_USER"),
                            System.getenv("MONGODB_AUTH_DATABASE"),
                            System.getenv("MONGODB_PASSWORD").toCharArray()
                        )
                    ).applyToClusterSettings {
                        it.hosts(
                            listOf(
                                ServerAddress(
                                    System.getenv("MONGODB_HOST"),
                                    System.getenv("MONGODB_PORT").toIntOrNull() ?: 27017
                                )
                            )
                        )
                    }.build()
            )
        }
        database = client.getDatabase("hg")
        isConnected = true
        return database
    }

    inline fun <reified T : Any> getOrCreateCollection(
        collectionName: String,
    ): MongoCollection<T> {
        runBlocking {
            if (collectionName !in database.listCollectionNames().toList()) {
                runCatching {
                    database.createCollection(collectionName)
                }.onFailure {
                    it.printStackTrace()
                    logger().warn("Error creating collection: $collectionName")
                }
            }
        }
        return database.getCollection<T>(collectionName)
    }
}
