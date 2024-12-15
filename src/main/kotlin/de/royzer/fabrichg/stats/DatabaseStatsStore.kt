package de.royzer.fabrichg.stats

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.stats.StatsStore.Companion.statsScope
import kotlinx.coroutines.*
import net.minecraft.world.entity.player.Player
import org.dizitart.kno2.getRepository
import org.dizitart.kno2.nitrite
import org.dizitart.kno2.serialization.KotlinXSerializationMapper
import org.dizitart.no2.Nitrite
import org.dizitart.no2.common.module.NitriteModule
import org.dizitart.no2.mvstore.MVStoreModule
import org.dizitart.no2.repository.ObjectRepository
import java.util.*

class DatabaseStatsStore: StatsStore {
    private lateinit var storeModule: MVStoreModule

    private lateinit var db: Nitrite
    private lateinit var repository: ObjectRepository<Stats>

    override fun init(): StatsStore {
        storeModule = MVStoreModule.withConfig()
            .filePath("stats.db")
            .build()
        db = nitrite {
            loadModule(storeModule)
            loadModule(NitriteModule.module(KotlinXSerializationMapper()))
        }

        repository = db.getRepository<Stats>()
        return this
    }

    override fun update(stats: Stats) {
        statsScope.launch {
            if (repository.getById(stats.uuid) == null) {
                repository.insert(stats)
            } else {
                repository.update(stats)
            }
        }
    }

    override fun get(player: Player): Deferred<Stats> {
        return get(player.uuid)
    }

    override fun get(uuid: UUID): Deferred<Stats> {
        return statsScope.async {
            val stats = repository.getById(uuid.toString())
            if (stats == null) {
                val newStats = Stats(uuid.toString())
                update(newStats)
                newStats
            } else stats
        }
    }

    override fun initPlayer(player: Player) {
        val stats = Stats(player.uuid.toString())
        statsScope.launch {
            val result = repository.getById(player.uuid.toString())
            if (result == null) {
                update(stats)
                return@launch
            } else player.hgPlayer!!.stats = result
        }
    }

}
