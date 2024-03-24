package de.royzer.fabrichg.data.hgplayer

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.bots.HGBot
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.combatlog.maxOfflineTime
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.kit.Kit
import de.royzer.fabrichg.kit.cooldown.hasCooldown
import de.royzer.fabrichg.kit.kits.neoKit
import de.royzer.fabrichg.mixins.world.CombatTrackerAcessor
import de.royzer.fabrichg.stats.Database
import de.royzer.fabrichg.stats.Stats
import de.royzer.fabrichg.settings.ConfigManager
import de.royzer.fabrichg.util.forceGiveItem
import de.royzer.fabrichg.util.kitSelector
import net.fabricmc.fabric.api.entity.FakePlayer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.silkmc.silk.core.item.setCustomName
import net.silkmc.silk.core.item.setLore
import net.silkmc.silk.core.text.literalText
import net.silkmc.silk.core.text.sendText
import java.util.*

class HGPlayer(
    val uuid: UUID,
    val name: String
) {
    var status: HGPlayerStatus = HGPlayerStatus.ALIVE
    var kills: Int = 0
    var offlineTime = maxOfflineTime
    val kits = mutableListOf<Kit>()
    var stats: Stats = Stats(uuid.toString())
        set(value) {
            field = value
            Database.updateOrCreateStats(value)
        }

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
                    serverPlayer?.forceGiveItem(item.itemStack.copy().also {
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
                serverPlayer?.forceGiveItem(item.itemStack.copy().also {
                    it.setLore(listOf(literalText("Kititem")))
                    if (!it.hasCustomHoverName()) {
                        it.setCustomName(singleKit.name)
                    }
                })
            }
            singleKit.onEnable?.invoke(this, singleKit, serverPlayer!!)
        }

    }

    fun addKit(kit: Kit) {
        if (!kit.enabled) {
            this.serverPlayer?.sendText {
                text("This kit is disabled")
                color = TEXT_GRAY
                bold = true
            }
            return
        }
        this.kits.clear() // solange nur 1 kit
        this.kits.add(kit)
        this.serverPlayer?.sendSystemMessage(
            literalText {
                text("You are now ") { color = TEXT_GRAY }
                text(kit.name) { color = TEXT_BLUE }
            }
        )
        if (GamePhaseManager.isIngame) {
            this.giveKitItems(kit)
        }
    }

    val isNeo get() = canUseKit(neoKit)

    val isAlive get() = status == HGPlayerStatus.ALIVE

    // vielleicht noch gucken dass nur player zählen
    val inFight: Boolean
        get() {
            val combatTracker = serverPlayer?.combatTracker ?: return false
            val lastCombatEntry = (combatTracker as CombatTrackerAcessor).entries.lastOrNull()
            return lastCombatEntry?.source?.entity is ServerPlayer
        }

    val isBot get() = this.serverPlayer is FakePlayer


    fun updateStats(kills: Int = 0, deaths: Int = 0, wins: Int = 0) {
        this.stats = this.stats.copy(
            kills = this.stats.kills + kills,
            deaths = this.stats.deaths + deaths,
            wins = this.stats.wins + wins
        )

    fun giveKitSelectors() {
        val kits = ConfigManager.gameSettings.kitAmount
        repeat(kits) {
            this.serverPlayer?.inventory?.add(kitSelector(it))
        }

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