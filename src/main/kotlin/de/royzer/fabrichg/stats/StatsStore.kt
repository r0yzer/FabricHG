package de.royzer.fabrichg.stats

import net.minecraft.world.entity.player.Player
import java.util.*

interface StatsStore {
    suspend fun init(): StatsStore
    suspend fun update(stats: Stats)
    suspend fun get(player: Player): Stats
    suspend fun get(uuid: UUID): Stats
    suspend fun getAll(): Iterable<Stats>
    suspend fun initPlayer(player: Player)
}
