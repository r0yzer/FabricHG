package de.royzer.fabrichg.events

import de.royzer.fabrichg.data.hgplayer.HGPlayerStatus
import de.royzer.fabrichg.data.hgplayer.hgPlayerData
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.broadcast
import de.royzer.fabrichg.game.combatlog.combatloggedPlayers
import de.royzer.fabrichg.game.combatlog.startCombatlog
import de.royzer.fabrichg.game.phase.PhaseType
import kotlinx.coroutines.cancel
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

            when (gamePhase) {
                PhaseType.LOBBY -> {
                    player.health = player.maxHealth
                    player.hungerManager.foodLevel = 40
                    player.changeGameMode(GameMode.ADVENTURE)
                    PlayerList.addPlayer(player.uuid)
                }
                PhaseType.INVINCIBILITY -> {
                    PlayerList.addPlayer(player.uuid)
                }
                PhaseType.INGAME -> {
                    if (uuid in combatloggedPlayers.keys) {
                        combatloggedPlayers[uuid]?.job?.cancel()
                        broadcast("${handler.player.name.asString()} ist wieder da")
                        player.hgPlayerData.status = HGPlayerStatus.ALIVE
                    } else {
                        player.hgPlayerData.status = HGPlayerStatus.SPECTATOR
                        player.changeGameMode(GameMode.SPECTATOR)
                        player.sendMessage(literalText("nunja gamne schon start") { }, false)
                    }
                }
            }
        }
        ServerPlayConnectionEvents.DISCONNECT.register { handler, server ->
            val uuid = handler.player.uuid
            when (GamePhaseManager.currentPhase.phaseType) {
                PhaseType.LOBBY -> PlayerList.removePlayer(uuid)
                PhaseType.END -> {
                }
                else -> {
                    if (handler.player.hgPlayerData.status == HGPlayerStatus.ALIVE) {
                        if (handler.player.damageTracker.mostRecentDamage?.attacker !is ServerPlayerEntity) {
                            handler.player.startCombatlog()
                        } else {
                            handler.player.health = 0F
                        }
                    }
                }
            }
        }
    }
}