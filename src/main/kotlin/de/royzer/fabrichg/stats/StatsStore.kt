package de.royzer.fabrichg.stats

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import net.minecraft.world.entity.player.Player
import java.util.*

interface StatsStore {
    companion object {
        val statsScope = CoroutineScope(Dispatchers.IO)
    }

    fun init(): StatsStore
    fun update(stats: Stats)
    fun get(player: Player): Deferred<Stats>
    fun get(uuid: UUID): Deferred<Stats>
    fun initPlayer(player: Player)
}
