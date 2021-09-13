package de.royzer.fabrichg.data.hgplayer

import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.combatlog.maxOfflineTime
import de.royzer.fabrichg.kit.Kit
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

class HGPlayer(
    val uuid: UUID,
    val name: String
) {
    var status: HGPlayerStatus = HGPlayerStatus.ALIVE
    var kills: Int = 0
    var offlineTime = maxOfflineTime
    val kits = mutableListOf<Kit>()

    val serverPlayerEntity get() = GamePhaseManager.server.playerManager.getPlayer(uuid)

    fun hasKit(kit: Kit) = kit in kits
}

val ServerPlayerEntity.hgPlayer
    get() = PlayerList.addOrGetPlayer(uuid, name.string)