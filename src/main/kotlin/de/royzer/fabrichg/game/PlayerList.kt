package de.royzer.fabrichg.game

import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

object PlayerList {
    val players = mutableSetOf<UUID>()

    fun addPlayer(uuid: UUID) {
        players.add(uuid)
    }

    fun removePlayer(uuid: UUID) {
        players.remove(uuid)
    }
}
