package de.royzer.fabrichg.gulag

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.mixins.server.MinecraftServerAccessor
import de.royzer.fabrichg.server
import de.royzer.fabrichg.util.toHighestPos
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.Items
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.text.literal
import net.silkmc.silk.core.text.literalText
import java.util.*

object GulagManager {
    val gulagLevel: ServerLevel
    val gulagQueue = LinkedList<HGPlayer>()
    val fighting = mutableListOf<HGPlayer>()

    init {
        val levels = (server as MinecraftServerAccessor).levelsMap

        println(levels.toList().map { pair -> pair.first.location().path })
        gulagLevel = levels.toList().find { pair -> pair.first.location().path == "gulag" }?.second ?: error("gulag world not loaded")
    }

    fun onDeath(killer: Entity?, player: ServerPlayer): Boolean {
        return onDeath(killer, player.hgPlayer)
    }

    fun onDeath(killer: Entity?, hgPlayer: HGPlayer): Boolean {
        val allPlayersGulag = PlayerList.alivePlayers
            .filter { player -> player != hgPlayer }
            .none { player -> !isInGulag(player) }

        if (allPlayersGulag) return false

        val wasInGulag = hgPlayer.getPlayerData<Boolean>("gulag") == true

        if (!wasInGulag) {
            sendToGulag(hgPlayer)

            return true
        }


        if (fighting.size != 2) return false
        val (player1, player2) = fighting
        val otherHgPlayer = if (hgPlayer == player1) player2 else player1


        if (fighting.contains(hgPlayer)) {
            fighting.clear()

            recheckQueue()

            val highest = BlockPos(0, 0, 0).toHighestPos()

            otherHgPlayer.serverPlayer?.teleportTo(server.overworld(), highest.x.toDouble(), highest.y.toDouble(),
                highest.z.toDouble(), 0f, 0f)
            otherHgPlayer.serverPlayer?.sendSystemMessage(literalText {
                text("nicht schlecht, du hast gegen ") {
                    color = TEXT_GRAY
                }
                text(hgPlayer.name) {
                    color = TEXT_BLUE
                }
                text(" gewonnen") {
                    color = TEXT_GRAY
                }
            })
        }

        return false
    }

    fun recheckQueue() {
        if (gulagQueue.size < 2) return

        val player1 = gulagQueue.poll()
        val player2 = gulagQueue.poll()

        startGulagFight(player1, player2)
        println("$player1 vs $player2")
    }

    fun sendToGulag(player: HGPlayer) {
        player.playerData["gulag"] = true

        val opp = gulagQueue.peek()

        val isFightEmpty = fighting.isEmpty()

        if (opp != null && isFightEmpty) {
            startGulagFight(opp, player)
            return
        }

        gulagQueue.add(player)
        player.serverPlayer?.teleportTo(gulagLevel, 0.0, 90.0, 20.0, 0.0F, 0.0F)
    }

    fun startGulagFight(player1: HGPlayer, player2: HGPlayer) {
        listOf(player1, player2).forEach { player ->
            if (gulagQueue.contains(player)) gulagQueue.remove(player)
        }

        fighting.add(player1)
        fighting.add(player2)

        player1.serverPlayer?.teleportTo(gulagLevel, 20.0, 90.0, 0.0, 0.0F, 0.0F)
        player2.serverPlayer?.teleportTo(gulagLevel, -20.0, 90.0, 0.0, 0.0F, 0.0F)

        player1.serverPlayer?.gulagInventory()
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

        return fighting.contains(hgPlayer)
    }

    fun isInGulag(hgPlayer: HGPlayer): Boolean {
        return gulagQueue.contains(hgPlayer) || fighting.contains(hgPlayer)
    }
}