package de.royzer.fabrichg.data.hgplayer

import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

class HGPlayer(
    val uuid: UUID,
    val name: String
) {
    var status: HGPlayerStatus = HGPlayerStatus.ALIVE
    var kills: Int = 0
    var offlineTime = 10

    val serverPlayerEntity get() = GamePhaseManager.server.playerManager.getPlayer(uuid)
}

val ServerPlayerEntity.hgPlayer
    get() = PlayerList.getPlayer(uuid, name.string)