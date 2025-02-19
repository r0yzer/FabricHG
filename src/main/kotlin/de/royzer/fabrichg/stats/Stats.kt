package de.royzer.fabrichg.stats

import de.royzer.fabrichg.mongodb.MongoManager
import de.royzer.fabrichg.serialization.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.player.Player
import java.util.*

@Serializable
data class Stats(
    @SerialName("_id") @Serializable(with = UUIDSerializer::class) val uuid: UUID,
    val kills: Int = 0,
    val deaths: Int = 0,
    val wins: Int = 0,
) {
    val score: Int get() = (kills * 3) + (wins * 15) - (deaths * 2)

    companion object : StatsStore {
        private lateinit var store: StatsStore

        override suspend fun init(): StatsStore {
            if (MongoManager.isConnected) {
                runCatching {
                    store = DatabaseStatsStore().init()
                    return this
                }
            }
            store = MemoryStatsStore().init()
            return this
        }

        override suspend fun update(stats: Stats) {
            store.update(stats)
        }

        override suspend fun get(player: Player): Stats {
            return store.get(player)
        }

        override suspend fun get(uuid: UUID): Stats {
            return store.get(uuid)
        }

        override suspend fun getAll(): Iterable<Stats> {
            return store.getAll()
        }

        override suspend fun initPlayer(player: Player) {
            store.initPlayer(player)
        }
    }
}
