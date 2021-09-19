package de.royzer.fabrichg.data.hgplayer

import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.combatlog.maxOfflineTime
import de.royzer.fabrichg.kit.Kit
import de.royzer.fabrichg.kit.cooldown.hasCooldown
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

    var kitsDisabled = false

    val serverPlayerEntity get() = GamePhaseManager.server.playerManager.getPlayer(uuid)

    fun hasKit(kit: Kit) = kit in kits

    fun canUseKit(kit: Kit) = hasKit(kit) && !hasCooldown(kit) && GamePhaseManager.isIngame && !kitsDisabled // TODO cooldown/rouge

    fun canUseKit(kit: Kit, ignoreCooldown: Boolean): Boolean {
        return if (ignoreCooldown) hasKit(kit) && GamePhaseManager.isIngame && !kitsDisabled
        else canUseKit(kit)
    }


}

val ServerPlayerEntity.hgPlayer
    get() = PlayerList.addOrGetPlayer(uuid, name.string)