package de.royzer.fabrichg.events

import de.royzer.fabrichg.data.hgplayer.HGPlayerStatus
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.broadcast
import de.royzer.fabrichg.game.combatlog.combatloggedPlayers
import de.royzer.fabrichg.game.combatlog.startCombatlog
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.game.phase.phases.LobbyPhase
import de.royzer.fabrichg.game.phase.phases.freeze
import de.royzer.fabrichg.game.removeHGPlayer
import de.royzer.fabrichg.kit.kits.NoneKit
import de.royzer.fabrichg.scoreboard.showScoreboard
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.job
import net.axay.fabrik.core.text.literalText
import net.axay.fabrik.core.text.sendText
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.GameMode

object ConnectEvents {
    init {
        ServerPlayConnectionEvents.JOIN.register { handler, sender, server ->
            val gamePhase = GamePhaseManager.currentPhase.phaseType
            val player = handler.player
            val uuid = player.uuid
            val hgPlayer = player.hgPlayer

            player.attributes.getCustomInstance(EntityAttributes.GENERIC_ATTACK_SPEED)?.baseValue = 100.0

            if (hgPlayer.kits.isEmpty())
                hgPlayer.kits.add(NoneKit)

            when (gamePhase) {
                PhaseType.LOBBY -> {
                    player.clearStatusEffects()
                    player.health = player.maxHealth
                    player.inventory.clear()
                    player.hungerManager.foodLevel = 20
                    player.changeGameMode(GameMode.ADVENTURE)
                    if (LobbyPhase.isStarting) player.freeze()
                    PlayerList.addOrGetPlayer(player.uuid, player.name.string)
                }
                PhaseType.INVINCIBILITY -> {
                    PlayerList.addOrGetPlayer(player.uuid, player.name.string)
                }
                PhaseType.INGAME -> {
                    when (player.hgPlayer.status) {
                        HGPlayerStatus.DISCONNECTED -> {
                            combatloggedPlayers[uuid]?.job?.cancel()
                            player.hgPlayer.status = HGPlayerStatus.ALIVE
                        }
                        else -> {
                            player.hgPlayer.status = HGPlayerStatus.SPECTATOR
                            player.changeGameMode(GameMode.SPECTATOR)
                            player.clearStatusEffects()
                            player.sendText(literalText("nunja gamne schon start") { })
                        }
                    }
                }
                PhaseType.END -> {
                    player.hgPlayer.status = HGPlayerStatus.SPECTATOR
                    player.changeGameMode(GameMode.SPECTATOR)
                    player.sendText(literalText("nunja gamne schon vorbei") { })
                }
            }

            player.showScoreboard()
        }
        ServerPlayConnectionEvents.DISCONNECT.register { handler, server ->
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
                    } else {
                        PlayerList.removePlayer(uuid)
                    }
                }
                PhaseType.END -> {}
            }
        }
    }
}