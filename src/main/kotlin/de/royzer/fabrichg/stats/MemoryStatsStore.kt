package de.royzer.fabrichg.stats

import de.royzer.fabrichg.stats.StatsStore.Companion.statsScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import net.minecraft.world.entity.player.Player
import java.util.*

class MemoryStatsStore: StatsStore {
    private val statsMap = hashMapOf<UUID, Stats>()
    override fun init(): StatsStore {
        return this
    }

    override fun update(stats: Stats) {
        statsMap[UUID.fromString(stats.uuid)] = stats
    }

    override fun get(player: Player): Deferred<Stats> {
        return get(player.uuid)
    }

    override fun get(uuid: UUID): Deferred<Stats> {
        return statsScope.async {
            statsMap.computeIfAbsent(uuid) { Stats(uuid.toString()) }
        }

    }
    override fun initPlayer(player: Player) {
        statsMap.computeIfAbsent(player.uuid) { Stats(player.uuid.toString()) }
    }
}
