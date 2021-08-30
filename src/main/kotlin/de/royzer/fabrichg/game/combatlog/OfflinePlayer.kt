package de.royzer.fabrichg.game.combatlog

import de.royzer.fabrichg.data.hgplayer.HGPlayerData
import de.royzer.fabrichg.game.GamePhaseManager
import kotlinx.coroutines.Job
import net.minecraft.server.PlayerManager
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

class OfflinePlayer(
    val name: String,
    val uuid: UUID,
    val hgPlayerData: HGPlayerData,
    var job: Job,
) {
    fun asServerPlayerEntity(): ServerPlayerEntity? {
        return GamePhaseManager.server.playerManager.getPlayer(uuid)
    }
}