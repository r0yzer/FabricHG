package de.royzer.fabrichg.stats

import kotlinx.coroutines.Deferred
import kotlinx.serialization.Serializable
import net.minecraft.world.entity.player.Player
import org.dizitart.no2.repository.Cursor
import org.dizitart.no2.repository.annotations.Id
import java.util.*

@Serializable
data class Stats(@Id val uuid: String, val kills: Int = 0, val deaths: Int = 0, val wins: Int = 0) {
    val score: Int get() = (kills * 3) + (wins * 15) - (deaths * 2)

    companion object: StatsStore {
        private lateinit var store: StatsStore

        override fun init(): StatsStore {
            runCatching {
                store = DatabaseStatsStore().init()
            }.onFailure {
                store = MemoryStatsStore().init()
            }
            return this
        }

        override fun update(stats: Stats) {
            store.update(stats)
        }

        override fun get(player: Player): Deferred<Stats> {
            return store.get(player)
        }

        override fun get(uuid: UUID): Deferred<Stats> {
            return store.get(uuid)
        }

        override fun getAll(): Deferred<Iterable<Stats>> {
            return store.getAll()
        }

        override fun initPlayer(player: Player) {
            store.initPlayer(player)
        }
    }
}
