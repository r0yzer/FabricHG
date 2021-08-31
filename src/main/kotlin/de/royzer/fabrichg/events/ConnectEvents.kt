package de.royzer.fabrichg.events

import de.royzer.fabrichg.data.hgplayer.HGPlayerStatus
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.broadcast
import de.royzer.fabrichg.game.combatlog.combatloggedPlayers
import de.royzer.fabrichg.game.combatlog.startCombatlog
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.game.removeHGPlayer
import de.royzer.fabrichg.scoreboard.showScoreboard
import kotlinx.coroutines.cancel
import kotlinx.coroutines.job
import net.axay.fabrik.core.sideboard.showSideboard
import net.axay.fabrik.core.text.literalText
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.GameMode

object ConnectEvents {
    init {
        ServerPlayConnectionEvents.JOIN.register { handler, sender, server ->
            val gamePhase = GamePhaseManager.currentPhase.phaseType
            val player = handler.player
            val uuid = player.uuid

            player.showScoreboard()

            when (gamePhase) {
                PhaseType.LOBBY -> {
                    player.health = player.maxHealth
                    player.hungerManager.foodLevel = 40
                    player.changeGameMode(GameMode.ADVENTURE)
                    PlayerList.getPlayer(player.uuid, player.name.string)
                }
                PhaseType.INVINCIBILITY -> {
                    PlayerList.getPlayer(player.uuid, player.name.string)
                }
                PhaseType.INGAME -> {

                    broadcast("${player.name.string} joined in ${gamePhase.name} als ${player.hgPlayer.status}")

                    when (player.hgPlayer.status) {
                        HGPlayerStatus.COMBATLOGGED -> {
                            combatloggedPlayers[uuid]?.job?.cancel()
                            player.hgPlayer.status = HGPlayerStatus.ALIVE
                        }
                        else -> {
                            player.hgPlayer.status = HGPlayerStatus.SPECTATOR
                            player.changeGameMode(GameMode.SPECTATOR)
                            player.sendMessage(literalText("nunja gamne schon start") { }, false)
                        }
                    }
                }
            }
        }
        ServerPlayConnectionEvents.DISCONNECT.register { handler, server ->
            broadcast("${handler.player.name.string} quit in ${GamePhaseManager.currentPhase.phaseType.name} als ${handler.player.hgPlayer.status}")
            val uuid = handler.player.uuid
            when (GamePhaseManager.currentPhase.phaseType) {
                PhaseType.LOBBY -> PlayerList.removePlayer(uuid)
                PhaseType.INVINCIBILITY -> {
                    handler.player.startCombatlog()
                }
                PhaseType.INGAME -> {
                    if (handler.player.hgPlayer.status == HGPlayerStatus.ALIVE) {
                        if (handler.player.damageTracker.mostRecentDamage?.attacker is ServerPlayerEntity) {
                            handler.player.health = 0F
                            handler.player.removeHGPlayer()
                            broadcast("tot weil in combat leaved tja")
                            PlayerList.announceRemainingPlayers()
                        } else {
                            handler.player.startCombatlog()
                        }
                    }
                }
                else -> {}
            }
        }
    }
}