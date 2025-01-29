package de.royzer.fabrichg.stats

import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.kotlin.client.coroutine.MongoCollection
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.mongodb.MongoManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import net.minecraft.world.entity.player.Player
import java.util.*

class DatabaseStatsStore : StatsStore {
    private lateinit var collection: MongoCollection<Stats>

    override suspend fun init(): StatsStore {
        collection = MongoManager.getOrCreateCollection("stats")
        return this
    }

    override suspend fun update(stats: Stats) {
        collection.replaceOne(Filters.eq("_id", stats.uuid), stats, ReplaceOptions().upsert(true))
    }

    override suspend fun get(player: Player): Stats {
        return get(player.uuid)
    }

    override suspend fun get(uuid: UUID): Stats {
        val stats = collection.find(Filters.eq("_id", uuid)).firstOrNull()
            ?: Stats(uuid).also { update(it) }
        return stats
    }

    override suspend fun getAll(): Iterable<Stats> {
        return collection.find().toList()
    }

    override suspend fun initPlayer(player: Player) {
        val result = get(player)
        player.hgPlayer!!.stats = result
    }
}
