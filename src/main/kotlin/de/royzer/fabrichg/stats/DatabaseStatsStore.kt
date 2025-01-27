package de.royzer.fabrichg.stats

import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.kotlin.client.coroutine.MongoCollection
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.mongodb.MongoManager
import de.royzer.fabrichg.stats.StatsStore.Companion.statsScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import net.minecraft.world.entity.player.Player
import java.util.*

class DatabaseStatsStore : StatsStore {
    private lateinit var collection: MongoCollection<Stats>

    override fun init(): StatsStore {
        collection = MongoManager.getOrCreateCollection("stats")
        return this
    }

    override fun update(stats: Stats) {
        statsScope.launch {
            collection.replaceOne(Filters.eq("_id", stats.uuid), stats, ReplaceOptions().upsert(true))
        }
    }

    override fun get(player: Player): Deferred<Stats> {
        return get(player.uuid)
    }

    override fun get(uuid: UUID): Deferred<Stats> {
        return statsScope.async {
            val stats = collection.find(Filters.eq("_id", uuid)).firstOrNull()
            if (stats == null) {
                val newStats = Stats(uuid)
                update(newStats)
                newStats
            } else stats
        }
    }

    override fun getAll(): Deferred<Iterable<Stats>> {
        return statsScope.async {
            collection.find().toList()
        }
    }

    override fun initPlayer(player: Player) {
        statsScope.launch {
            val result = get(player).await()
            player.hgPlayer!!.stats = result
        }
    }
}
