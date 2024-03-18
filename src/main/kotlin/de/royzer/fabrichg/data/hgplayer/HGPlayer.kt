package de.royzer.fabrichg.data.hgplayer

import de.royzer.fabrichg.bots.HGBot
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.combatlog.maxOfflineTime
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.kit.Kit
import de.royzer.fabrichg.kit.cooldown.hasCooldown
import de.royzer.fabrichg.kit.kits.neoKit
import de.royzer.fabrichg.mixins.world.CombatTrackerAcessor
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.silkmc.silk.core.item.setCustomName
import net.silkmc.silk.core.item.setLore
import net.silkmc.silk.core.text.literalText
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
        get() = GamePhaseManager.server.playerList.getPlayerByName(name)
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

    /**
     * @param singleKit null wenn die items aller kits gegeben werden sollen sonst das kit
     */
    fun giveKitItems(singleKit: Kit? = null) {
        if (singleKit == null) {
            kits.forEach { kit ->
                kit.kitItems.forEach { item ->
                    serverPlayer?.inventory?.add(item.itemStack.copy().also {
                        it.setLore(listOf(literalText("Kititem")))
                        if (!it.hasCustomHoverName()) {
                            it.setCustomName(kit.name)
                        }
                    })
                }
                kit.onEnable?.invoke(this, kit, serverPlayer!!)
            }
        } else {
            singleKit.kitItems.forEach { item ->
                serverPlayer?.inventory?.add(item.itemStack.copy().also {
                    it.setLore(listOf(literalText("Kititem")))
                    if (!it.hasCustomHoverName()) {
                        it.setCustomName(singleKit.name)
                    }
                })
            }
            singleKit.onEnable?.invoke(this, singleKit, serverPlayer!!)
        }

    }

    val isNeo get() = canUseKit(neoKit)
    
    val isAlive get() = status == HGPlayerStatus.ALIVE

    // vielleicht noch gucken dass nur player zählen
    val inFight: Boolean get() {
        val combatTracker = serverPlayer?.combatTracker ?: return false
        val lastCombatEntry = (combatTracker as CombatTrackerAcessor).entries.lastOrNull()
        return lastCombatEntry?.source?.entity is ServerPlayer
    }

    override fun toString(): String {
        return "HGPlayer ${this.name}, Kits: [${kits.joinToString()}]"
    }
}

val ServerPlayer.hgPlayer
    get() = PlayerList.addOrGetPlayer(uuid, name.string)

val HGBot.hgPlayer
    get() = PlayerList.addOrGetPlayer(uuid, name.string)

val Entity.hgPlayer
    get() = when (this) {
        is ServerPlayer -> hgPlayer
        is HGBot -> hgPlayer
        else -> null
    }