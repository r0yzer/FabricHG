package de.royzer.fabrichg.gulag

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.data.hgplayer.HGPlayerStatus
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.mixins.server.MinecraftServerAccessor
import de.royzer.fabrichg.server
import de.royzer.fabrichg.settings.ConfigManager
import de.royzer.fabrichg.util.dropInventoryItemsWithoutKitItems
import de.royzer.fabrichg.util.toHighestPos
import net.minecraft.commands.arguments.EntityAnchorArgument
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.Items
import net.minecraft.world.phys.Vec3
import net.silkmc.silk.core.entity.changePos
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.text.broadcastText
import net.silkmc.silk.core.text.literalText
import java.util.*

object GulagManager {
    val gulagLevel: ServerLevel
    val gulagQueue = LinkedList<HGPlayer>()
    val fighting = mutableListOf<HGPlayer>()

    val gulagEnabled: Boolean get() = ConfigManager.gameSettings.gulagEnabled
    val gulagEndTime: Int get() = ConfigManager.gameSettings.gulagEndTime
    val minPlayersOutsideGulag: Int get() = ConfigManager.gameSettings.minPlayersOutsideGulag

    val empty: Boolean get() = gulagQueue.isEmpty() && fighting.isEmpty()

    init {
        val levels = (server as MinecraftServerAccessor).levelsMap

        gulagLevel = levels.toList().find { pair -> pair.first.location().path == "gulag" }?.second
            ?: error("gulag world not loaded")
    }

    fun getOpponent(player: HGPlayer): HGPlayer? {
        if (fighting.size != 2) return null
        if (!fighting.contains(player)) return null

        val (player1, player2) = fighting
        return if (player == player1) player2 else player1
    }

    fun onWin(player: HGPlayer, loser: HGPlayer) {
        val highest = BlockPos(0, 0, 0).toHighestPos()

        player.status = HGPlayerStatus.ALIVE
        loser.status = HGPlayerStatus.SPECTATOR

        server.broadcastText(literalText {
            text(player.name) {
                color = TEXT_BLUE
                underline = true
            }
            text(" hat gegen ") {
                color = TEXT_GRAY
            }
            text(loser.name) {
                color = TEXT_BLUE
            }
            text(" gewonnen") {
                color = TEXT_GRAY
            }
        })

        player.serverPlayer?.changePos(highest.x, highest.y, highest.z, server.overworld())
        loser.serverPlayer?.changePos(highest.x, highest.y, highest.z, server.overworld())

        fighting.clear()

        recheckQueue()
    }

    fun onDisconnect(player: HGPlayer) {
        PlayerList.removePlayer(player.uuid)

        if (isFighting(player)) {
            val opponent = getOpponent(player)

            if (opponent != null) {
                onWin(opponent, player)
            }
        } else if (gulagQueue.contains(player)) {
            gulagQueue.remove(player)
        }
    }

    fun beforeDeath(killer: Entity?, player: ServerPlayer): Boolean {
        return beforeDeath(killer, player.hgPlayer)
    }

    fun canGoToGulag(player: HGPlayer): Boolean {
        if (!gulagEnabled) return false
        if (GamePhaseManager.timer.toLong() > gulagEndTime) return false

        val playersNotInGulag = PlayerList.alivePlayers
            .filter { player -> !isInGulag(player) }

        if (playersNotInGulag.size < minPlayersOutsideGulag) return false

        val wasInGulag = player.getPlayerData<Boolean>("gulag") == true
        return !wasInGulag
    }

    fun beforeDeath(killer: Entity?, hgPlayer: HGPlayer): Boolean {
        if (!canGoToGulag(hgPlayer)) return false

        val wasInGulag = hgPlayer.getPlayerData<Boolean>("gulag") == true

        if (!wasInGulag) {
            hgPlayer.serverPlayer?.dropInventoryItemsWithoutKitItems()

            sendToGulag(hgPlayer)

            return true
        }

        return false
    }

    fun afterDeath(killer: Entity?, player: ServerPlayer) {
        afterDeath(killer, player.hgPlayer)
    }

    fun afterDeath(killer: Entity?, hgPlayer: HGPlayer) {
        if (fighting.contains(hgPlayer)) {
            val otherHgPlayer = getOpponent(hgPlayer)

            if (otherHgPlayer == null) return

            onWin(otherHgPlayer, hgPlayer)
        }
    }

    fun recheckQueue() {
        if (gulagQueue.size < 2) return

        val player1 = gulagQueue.poll()
        val player2 = gulagQueue.poll()

        startGulagFight(player1, player2)
    }

    fun sendToGulag(player: HGPlayer) {
        player.status = HGPlayerStatus.GULAG
        player.playerData["gulag"] = true

        val opp = gulagQueue.peek()

        val isFightEmpty = fighting.isEmpty()

        if (opp != null && isFightEmpty) {
            startGulagFight(opp, player)
            return
        }

        gulagQueue.add(player)

        // (player.serverPlayer as? FakeServerPlayer?)?.hgBot?.changePos(0, 90, 0, gulagLevel)
        player.serverPlayer?.teleportTo(gulagLevel, 0.0, 90.0, 20.0, 0.0F, 0.0F)
    }

    fun startGulagFight(player1: HGPlayer, player2: HGPlayer) {
        listOf(player1, player2).forEach { player ->
            if (gulagQueue.contains(player)) gulagQueue.remove(player)
        }

        fighting.add(player1)
        fighting.add(player2)

        val center = Vec3(0.0, 90.0, 0.0)

        player1.serverPlayer?.teleportTo(gulagLevel, center.x + 20, center.y, center.z, 0.0F, 0.0F)
        player1.serverPlayer?.lookAt(EntityAnchorArgument.Anchor.EYES, center)
        player1.serverPlayer?.gulagInventory()


        player2.serverPlayer?.teleportTo(gulagLevel,  center.x - 20, center.y, center.z, 0.0F, 0.0F)
        player2.serverPlayer?.lookAt(EntityAnchorArgument.Anchor.EYES, center)
        player2.serverPlayer?.gulagInventory()
    }

    fun ServerPlayer.gulagInventory() {
        val soup = itemStack(Items.MUSHROOM_STEW) { }
        val sword = itemStack(Items.STONE_SWORD) { }

        repeat(36) {
            inventory.setItem(it, soup.copy())
        }

        inventory.setItem(0, sword.copy())
    }

    fun isFighting(entity: Entity?): Boolean {
        val hgPlayer = entity?.hgPlayer ?: return false

        return isFighting(hgPlayer)
    }

    fun isFighting(hgPlayer: HGPlayer): Boolean {
        return fighting.contains(hgPlayer)
    }

    fun isInGulag(hgPlayer: HGPlayer): Boolean {
        return gulagQueue.contains(hgPlayer) || fighting.contains(hgPlayer)
    }
}