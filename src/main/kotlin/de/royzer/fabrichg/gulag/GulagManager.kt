package de.royzer.fabrichg.gulag

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.data.hgplayer.HGPlayerStatus
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.broadcastComponent
import de.royzer.fabrichg.game.removeHGPlayer
import de.royzer.fabrichg.mixins.entity.LivingEntityAccessor
import de.royzer.fabrichg.mixins.server.MinecraftServerAccessor
import de.royzer.fabrichg.sendPlayerStatus
import de.royzer.fabrichg.server
import de.royzer.fabrichg.settings.ConfigManager
import de.royzer.fabrichg.util.dropInventoryItemsWithoutKitItems
import de.royzer.fabrichg.util.getRandomHighestPos
import de.royzer.fabrichg.util.tracker
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import net.minecraft.commands.arguments.EntityAnchorArgument
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.Items
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.GameType
import net.minecraft.world.phys.Vec3
import net.silkmc.silk.core.entity.changePos
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.logging.logWarning
import net.silkmc.silk.core.task.mcCoroutineTask
import net.silkmc.silk.core.text.broadcastText
import net.silkmc.silk.core.text.literalText
import java.util.*
import kotlin.time.Duration.Companion.seconds

object GulagManager {
    val gulagLevel: ServerLevel
    val gulagQueue = LinkedList<HGPlayer>()
    val fighting = mutableListOf<HGPlayer>()

    var open = true

    val gulagEnabled: Boolean get() = ConfigManager.gameSettings.gulagEnabled
    val gulagEndTime: Int get() = ConfigManager.gameSettings.gulagEndTime
    val minPlayersOutsideGulag: Int get() = ConfigManager.gameSettings.minPlayersOutsideGulag

    val empty: Boolean get() = gulagQueue.isEmpty() && fighting.isEmpty()

    private var currentFightJob: Job? = null

    init {
        open = gulagEnabled
        val levels = (server as MinecraftServerAccessor).levelsMap

        gulagLevel = levels.toList().find { pair -> pair.first.location().path == "gulag" }?.second
            ?: error("gulag world not loaded")

        gulagLevel.gameRules.getRule(GameRules.RULE_DOMOBSPAWNING).set(false, server)
    }

    // schliesst es also man kann nicht mehr rein aber die drin sind machen noch zu ende
    // wird nach close time oder wenn nicht mehr genug player leben aufgerufen
    fun close(sendMessage: Boolean = true) {
        if (!open) {
            logWarning("Gulag wird geschlossen obwohl schon zu")
            return
        }
        open = false
        // fight job wird nicht gecancelt weil könnten ja noch welche fighten
        if (sendMessage) {
            broadcastComponent(literalText {
                text("Das Gulag ist nun ")
                text("geschlossen") { color = TEXT_BLUE }
                color = TEXT_GRAY
            })
        }


        // fighting ist empty also ist entweder einer oder keiner am warten also kann man das aufrufen
        // wenn mehr als 2 sind wird ja nach fight ende nochmal geguckt
        if (fighting.isEmpty()) {
            recheckQueue()
        }

    }

    fun getOpponent(player: HGPlayer): HGPlayer? {
        if (fighting.size != 2) return null
        if (!fighting.contains(player)) return null

        val (player1, player2) = fighting
        return if (player == player1) player2 else player1
    }

    fun respawnPlayer(player: HGPlayer, loser: HGPlayer?) {
        val highest = getRandomHighestPos(200)

        val serverPlayer = player.serverPlayer ?: return

        serverPlayer.setGameMode(GameType.SURVIVAL)

        player.status = HGPlayerStatus.ALIVE
        loser?.status = HGPlayerStatus.SPECTATOR

        server.broadcastText(literalText {
            text(player.name) {
                color = TEXT_BLUE
                underline = true
            }
            text(" ist aus dem Gulag zurückgekehrt") {
                color = TEXT_GRAY
            }
        })


        serverPlayer.inventory.clearContent()
        player.kitsDisabled = false
        player.allKits.forEach {
            it.onEnable?.invoke(player, it, serverPlayer)
        }
        player.giveKitItems()
        serverPlayer.inventory.add(itemStack(Items.STONE_SWORD) {count = 2})
        serverPlayer.inventory.add(itemStack(Items.MUSHROOM_STEW) {count = 34})
        serverPlayer.inventory.setItem(8, tracker)
        serverPlayer.changePos(highest.x, highest.y, highest.z, server.overworld())
        serverPlayer.health = 40f
        loser?.serverPlayer?.changePos(highest.x, highest.y, highest.z, server.overworld())

        fighting.clear()

        recheckQueue()
    }

    // wird aufgerufen wenn jemand während PhaseType == INGAME und status GULAG leaved
    fun onDisconnect(player: HGPlayer) {
        player.status = HGPlayerStatus.SPECTATOR

        broadcastComponent(literalText {
            text("${player.name} ist im Gulag geleaved")
            color = 0xFFFF55
        })
        PlayerList.announceRemainingPlayers()

        if (isFighting(player)) {
            val opponent = getOpponent(player)

            if (opponent != null) {
                respawnPlayer(opponent, player)
            }
        } else if (gulagQueue.contains(player)) {
            gulagQueue.remove(player)
        }
    }

    fun canGoToGulag(player: HGPlayer): Boolean {
        if (!open) return false // guckt auch ob enabled ist oder schon zu spät oder zu wenig spieler weil das false ist wenn close() aufgerufen wurde

        val wasInGulag = player.getPlayerData<Boolean>("gulag") == true
        return !wasInGulag
    }

    // guckt ob player ins gulag kann und cancelt dementsprechend den tod
    fun beforeDeath(killer: Entity?, player: ServerPlayer, source: DamageSource): Boolean {
        if ((player as LivingEntityAccessor).invokeTryUseTotem(source)) { // ?! kein plan wieso nicht ghet
            player.sendPlayerStatus()

            return false
        }

        val hgPlayer = player.hgPlayer
        if (!canGoToGulag(hgPlayer)) return false

        val wasInGulag = hgPlayer.getPlayerData<Boolean>("gulag") == true

        if (!wasInGulag) {
            hgPlayer.serverPlayer?.dropInventoryItemsWithoutKitItems()

            sendToGulag(hgPlayer)

            return true
        }

        return false
    }

    // wird in mixin aufgerufen
    fun afterDeath(killer: Entity?, player: ServerPlayer) {
        val hgPlayer = player.hgPlayer
        if (fighting.contains(hgPlayer)) {
            val otherHgPlayer = getOpponent(hgPlayer) ?: return

            respawnPlayer(otherHgPlayer, hgPlayer)
        }
    }

    fun recheckQueue() {
        if (gulagQueue.isEmpty()) return
        if (gulagQueue.size == 1) {
            if (!open) {
                val lastPlayer = gulagQueue.poll() ?: return
                // free win msg
                lastPlayer.serverPlayer?.sendSystemMessage(literalText {
                    text("You were the last player in the gulag and returned without a fight")
                    color = TEXT_GRAY
                })
                currentFightJob?.cancel()
                currentFightJob = null
                respawnPlayer(lastPlayer, null)
            } else {
                return
            }
        }

        val player1 = gulagQueue.poll() ?: return
        val player2 = gulagQueue.poll() ?: return

        startGulagFight(player1, player2)
    }

    fun sendToGulag(player: HGPlayer) {
        player.status = HGPlayerStatus.GULAG
        player.playerData["gulag"] = true

        player.kitsDisabled = true
        player.allKits.forEach {
            it.onDisable?.invoke(player, it)
        }

        player.serverPlayer?.let { serverPlayer ->
            serverPlayer.health = 40f
            serverPlayer.foodData.foodLevel = 20
        }

        val opp = gulagQueue.peek()

        val isFightEmpty = fighting.isEmpty()

        if (opp != null && isFightEmpty) {
            startGulagFight(opp, player)
            return
        }

        gulagQueue.add(player)
        player.serverPlayer?.setGameMode(GameType.ADVENTURE)
        // (player.serverPlayer as? FakeServerPlayer?)?.hgBot?.changePos(0, 90, 0, gulagLevel)
        player.serverPlayer?.teleportTo(gulagLevel, 2.48, 72.0, 27.56, 180.0F, 0.0F)
    }

    fun startGulagFight(player1: HGPlayer, player2: HGPlayer) {
        listOf(player1, player2).forEach { player ->
            if (gulagQueue.contains(player)) gulagQueue.remove(player)

            player.serverPlayer?.closeContainer()
        }

        fighting.add(player1)
        fighting.add(player2)

        val center = Vec3(0.0, 64.0, 0.98)

        player1.serverPlayer?.teleportTo(gulagLevel, center.x + 17.5, center.y, center.z, 0.0F, 0.0F)
        player1.serverPlayer?.lookAt(EntityAnchorArgument.Anchor.EYES, center)
        player1.serverPlayer?.giveGulagInventory()
        player1.serverPlayer?.playNotifySound(SoundEvents.GOAT_HORN_PLAY, SoundSource.HOSTILE, 100f, 1f)


        player2.serverPlayer?.teleportTo(gulagLevel,  center.x - 17.5, center.y, center.z, 180.0F, 0.0F)
        player2.serverPlayer?.lookAt(EntityAnchorArgument.Anchor.EYES, center)
        player2.serverPlayer?.giveGulagInventory()
        player2.serverPlayer?.playNotifySound(SoundEvents.GOAT_HORN_PLAY, SoundSource.HOSTILE, 100f, 1f)

        currentFightJob?.cancel() // sollte eigentlich null sein
        currentFightJob = mcCoroutineTask(period = 1.seconds, howOften = 60L) {
            when (it.round) {
                30L, 40L, 50L, 55L, 56L, 57L, 58L, 59L -> {
                    fighting.forEach { hgPlayer ->
                        hgPlayer.serverPlayer?.sendSystemMessage(
                            literalText {
                                text("Der Kampf endet in ")
                                text((60L - it.round).toString()) {color = TEXT_BLUE}
                                text(" Sekunden")
                                color = TEXT_GRAY
                            }
                        )
                    }
                }
                60L -> {
                    fighting.forEach { hgPlayer ->
                        hgPlayer.serverPlayer?.removeHGPlayer()
                        hgPlayer.serverPlayer?.sendSystemMessage(
                            literalText {
                                text("Der Kampf ist ")
                                text("vorbei") {color = TEXT_BLUE}
                                color = TEXT_GRAY
                            }
                        )
                    }
                    fighting.clear()
                    currentFightJob?.cancel("kosta titan — heute um 23:29 Uhrbist auf helm")
                    currentFightJob = null
                    recheckQueue()
                }

            }
        }
    }

    fun ServerPlayer.giveGulagInventory() {
        inventory.clearContent()
        removeAllEffects()
        health = 40.0f
        val soup = itemStack(Items.MUSHROOM_STEW) { }
        val sword = itemStack(Items.WOODEN_SWORD) { }

        repeat(9) {
            inventory.setItem(it, soup.copy())
        }

        inventory.setItem(0, sword.copy())
    }

    fun isFighting(entity: Entity?): Boolean {
        val hgPlayer = entity?.hgPlayer ?: return false

        return isFighting(hgPlayer)
    }

    fun isWaiting(entity: Entity?): Boolean {
        val hgPlayer = entity?.hgPlayer ?: return false

        return gulagQueue.contains(hgPlayer)
    }

    fun isFighting(hgPlayer: HGPlayer): Boolean {
        return fighting.contains(hgPlayer)
    }

    fun isInGulag(hgPlayer: HGPlayer): Boolean {
        return gulagQueue.contains(hgPlayer) || fighting.contains(hgPlayer)
    }
}
