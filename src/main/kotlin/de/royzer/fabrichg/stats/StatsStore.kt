package de.royzer.fabrichg.stats

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import net.minecraft.world.entity.player.Player
import org.dizitart.no2.repository.Cursor
import java.util.*

interface StatsStore {
    companion object {
        val statsScope = CoroutineScope(Dispatchers.IO)
    }

    fun init(): StatsStore
    fun update(stats: Stats)
    fun get(player: Player): Deferred<Stats>
    fun get(uuid: UUID): Deferred<Stats>
    fun getAll(): Deferred<Iterable<Stats>>
    fun initPlayer(player: Player)
}
