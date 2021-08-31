package de.royzer.fabrichg.game.combatlog

import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import kotlinx.coroutines.Job
import net.minecraft.server.PlayerManager
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

class OfflinePlayer(
    val name: String,
    val uuid: UUID,
    var combatlogTime: Int = 60,
    var job: Job,
) {
    fun asServerPlayerEntity(): ServerPlayerEntity? {
        return GamePhaseManager.server.playerManager.getPlayer(uuid)
    }
    val hgPlayer
        get() = PlayerList.getPlayer(uuid)
}