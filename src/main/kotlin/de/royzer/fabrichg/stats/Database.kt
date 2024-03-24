package de.royzer.fabrichg.stats

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import kotlinx.coroutines.*
import net.minecraft.world.entity.player.Player
import org.dizitart.kno2.getRepository
import org.dizitart.kno2.nitrite
import org.dizitart.kno2.serialization.KotlinXSerializationMapper
import org.dizitart.no2.common.module.NitriteModule
import org.dizitart.no2.mvstore.MVStoreModule
import java.util.*

object Database {
    private val dbScope = CoroutineScope(Dispatchers.IO)

    private val storeModule: MVStoreModule = MVStoreModule.withConfig()
        .filePath("stats.db")
        .build()

    private val db = nitrite {
        loadModule(storeModule)
        loadModule(NitriteModule.module(KotlinXSerializationMapper()))
    }
    private val repository = db.getRepository<Stats>()

    fun updateOrCreateStats(stats: Stats) {
        dbScope.launch {
            if (repository.getById(stats.uuid) == null) {
                repository.insert(stats)
            } else {
                repository.update(stats)
            }
        }
    }

    fun getStatsForPlayer(uuid: UUID): Deferred<Stats> {
        return dbScope.async {
            val stats = repository.getById(uuid.toString())
            if (stats == null) {
                val newStats = Stats(uuid.toString())
                updateOrCreateStats(newStats)
                newStats
            } else stats
        }
    }

    fun initPlayer(player: Player) {
        val stats = Stats(player.uuid.toString())
        dbScope.launch {
            val result = repository.getById(player.uuid.toString())
            if (result == null) {
                updateOrCreateStats(stats)
                return@launch
            } else player.hgPlayer!!.stats = result
        }
    }

}
