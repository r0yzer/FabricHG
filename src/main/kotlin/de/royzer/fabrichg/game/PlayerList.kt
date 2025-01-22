package de.royzer.fabrichg.game

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.TEXT_YELLOW_CHAT
import de.royzer.fabrichg.bots.HGBot
import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.data.hgplayer.HGPlayerStatus
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.game.phase.phases.IngamePhase
import de.royzer.fabrichg.gulag.GulagManager
import net.silkmc.silk.core.text.literalText
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.GameType
import net.silkmc.silk.core.text.sendText
import java.util.*

object PlayerList {
    val players = mutableMapOf<UUID, HGPlayer>()

    val aliveOrGulagPlayers get() = players.values.filter { it.status == HGPlayerStatus.ALIVE || it.status == HGPlayerStatus.DISCONNECTED || it.status == HGPlayerStatus.GULAG }
    val alivePlayers get() = players.values.filter { it.status == HGPlayerStatus.ALIVE || it.status == HGPlayerStatus.DISCONNECTED }

    val spectators get() = players.values.filter { it.status == HGPlayerStatus.SPECTATOR }

    val maxPlayers: Int
        get() = if (GamePhaseManager.currentPhaseType == PhaseType.INGAME || GamePhaseManager.currentPhaseType == PhaseType.END) IngamePhase.maxPlayers else alivePlayers.size

    fun addOrGetPlayer(uuid: UUID, name: String): HGPlayer {
        return players.getOrPut(uuid) {
            HGPlayer(uuid, name)
        }
    }

    fun getPlayer(uuid: UUID): HGPlayer? {
        return players[uuid]
    }

    fun removePlayer(uuid: UUID) {
        players.remove(uuid)
    }


    fun announcePlayerDeath(deadPlayer: HGPlayer, source: DamageSource, killer: Entity?, gulag: Boolean = false) {
        val sourceKiller = source.entity
        broadcastComponent(
            literalText {
                if (killer == sourceKiller && killer != null) { // killer ist ein entity und tötet hgplayer selber direkt
                    if (killer is ServerPlayer) {
                        text(
                            "${deadPlayer.name}(${deadPlayer.allKitsString}) was killed by ${killer.name.string}(${killer.hgPlayer.allKitsString}) using ${
                                killer.mainHandItem?.item.toString().uppercase()
                            }"
                        )
                    }
                    else if (killer is HGBot) { // hgbot und player haben beide mainhanditem aber nicht aus der gleichen subklasse oder so
                        text(
                            "${deadPlayer.name}(${deadPlayer.allKitsString}) was killed by ${killer.name.string}(${killer.hgPlayer?.allKitsString}) using ${
                                killer.mainHandItem?.item.toString().uppercase()
                            }"
                        )
                    } else { // mob
                        text("${deadPlayer.name}(${deadPlayer.allKitsString}) was killed by ${killer.name.string}")
                    }
                } else if (killer != null) { // source ist nicht der killer selber aber tötet indirekt oder halt creeper etc
                    if (killer is ServerPlayer) {
                        text("${deadPlayer.name}(${deadPlayer.allKitsString}) was killed by ${killer.name.string}(${killer.hgPlayer.allKitsString})")
                    } else if (killer is HGBot){
                        text("${deadPlayer.name}(${deadPlayer.allKitsString}) was killed by ${killer.name.string}(${killer.hgPlayer?.allKitsString})")
                    } else { // mob
                        text("${deadPlayer.name}(${deadPlayer.allKitsString}) was killed by ${killer.name.string}")
                    }
                } else { // ohne fremdeinwirkung
                    val prefix = "${deadPlayer.name}(${deadPlayer.allKitsString})"
                    when (val cause = source.msgId) {
                        "cactus" -> text("$prefix died from a cactus")
                        "fireball" -> text("$prefix died from a fireball")
                        "generickill" -> text("$prefix was killed")
                        "fall" -> text("$prefix fell from a high place")
                        "lightning_bolt" -> text("$prefix was struck by lightning")
                        else -> text("$prefix was killed by ${cause.uppercase()}")
                    }
                }
                color = TEXT_YELLOW_CHAT
            }
        )
        announceRemainingPlayers()
        if (alivePlayers.size < GulagManager.minPlayersOutsideGulag) {
            GulagManager.close()
        }
    }


    fun announceRemainingPlayers() {
        broadcastComponent(
            literalText {
                val players = alivePlayers.size
                text("$players player${if (players == 1) "" else "s"} remaining") {
                    color = TEXT_YELLOW_CHAT
                }
            }
        )
    }
}

fun ServerPlayer.removeHGPlayer() {
    hgPlayer.kits.forEach {
        it.onDisable?.invoke(hgPlayer, it)
    }
    hgPlayer.status = HGPlayerStatus.SPECTATOR
    setGameMode(GameType.SPECTATOR)
    sendText {
        text("Use the ")
        text("/info ") {
            bold = true
            color = TEXT_BLUE
        }
        text("command to get information about this round")
        italic = false
        color = TEXT_GRAY
    }
}


