package de.royzer.fabrichg.kit.cooldown

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.kit.Kit
import net.axay.fabrik.core.logging.logInfo

data class Cooldown(
    val hgPlayer: HGPlayer,
    val kit: Kit,
)

/**
 * @return true if cooldown was applied
 */
fun HGPlayer.checkUsesForCooldown(kit: Kit, maxUses: Int): Boolean {
    val key = kit.name + "uses"
    val uses = this.getPlayerData<Int>(key) ?: 1
    return if (uses >= maxUses) {
        this.activateCooldown(kit)
        this.playerData.remove(key)
        true
    } else {
        this.playerData[key] = uses + 1
        false
    }
}