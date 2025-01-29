package de.royzer.fabrichg.stats

import net.minecraft.world.entity.player.Player
import java.util.*

class MemoryStatsStore: StatsStore {
    private val statsMap = hashMapOf<UUID, Stats>()
    override suspend fun init(): StatsStore {
        return this
    }

    override suspend fun update(stats: Stats) {
        statsMap[stats.uuid] = stats
    }

    override suspend fun get(player: Player): Stats {
        return get(player.uuid)
    }

    override suspend  fun get(uuid: UUID): Stats {
        return statsMap.computeIfAbsent(uuid) { Stats(uuid) }
    }

    override suspend fun getAll(): Iterable<Stats> {
        return statsMap.values
    }

    override suspend fun initPlayer(player: Player) {
        statsMap.computeIfAbsent(player.uuid) { Stats(player.uuid) }
    }
}
