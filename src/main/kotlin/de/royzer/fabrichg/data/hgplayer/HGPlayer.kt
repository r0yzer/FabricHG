package de.royzer.fabrichg.data.hgplayer

import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.combatlog.maxOfflineTime
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.kit.Kit
import de.royzer.fabrichg.kit.cooldown.hasCooldown
import net.minecraft.server.level.ServerPlayer
import net.silkmc.silk.core.logging.logInfo
import java.util.*

class HGPlayer(
    val uuid: UUID,
    val name: String
) {
    var status: HGPlayerStatus = HGPlayerStatus.ALIVE
    var kills: Int = 0
    var offlineTime = maxOfflineTime
    val kits = mutableListOf<Kit>()

    val playerData = mutableMapOf<String, Any?>()

    inline fun <reified T> getPlayerData(key: String): T? {
        return playerData[key] as? T
    }

    var kitsDisabled = false

    val serverPlayer: ServerPlayer?
        get() {
//            logInfo("$name wird gegettet, uuid $uuid")
//            logInfo(GamePhaseManager.server.playerList.getPlayer(uuid)?.name?.string + " abc")
            return GamePhaseManager.server.playerList.getPlayerByName(name)
        }
    val serverPlayerOrException
        get() = GamePhaseManager.server.playerList.getPlayer(uuid) ?: error("HGPlayer has no ServerPlayer")

    /**
     * @return True if player has kit, even if currently not useable
     */
    fun hasKit(kit: Kit) = kit in kits

    /**
     * @return True if player has kit and kit is currently useable
     */
    fun canUseKit(kit: Kit): Boolean {
        return if (kit.usableInInvincibility)
            hasKit(kit) && !hasCooldown(kit) && GamePhaseManager.isIngame && !kitsDisabled
        else
            hasKit(kit) && !hasCooldown(kit) && GamePhaseManager.currentPhaseType == PhaseType.INGAME && !kitsDisabled
    }

    /**
     * @return True if player has kit and kit is currently useable
     */
    fun canUseKit(kit: Kit, ignoreCooldown: Boolean): Boolean {
        return if (ignoreCooldown) hasKit(kit) && GamePhaseManager.isIngame && !kitsDisabled
        else canUseKit(kit)
    }
}

val ServerPlayer.hgPlayer
    get() = PlayerList.addOrGetPlayer(uuid, name.string)