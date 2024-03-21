package de.royzer.fabrichg.events

import de.royzer.fabrichg.data.hgplayer.HGPlayerStatus
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.*
import de.royzer.fabrichg.game.combatlog.combatloggedPlayers
import de.royzer.fabrichg.game.combatlog.startCombatlog
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.kit.kits.noneKit
import de.royzer.fabrichg.kit.kits.onAnchorJoin
import de.royzer.fabrichg.mixins.world.CombatTrackerAcessor
import de.royzer.fabrichg.scoreboard.showScoreboard
import de.royzer.fabrichg.util.gameSettingsItem
import de.royzer.fabrichg.util.kitSelector
import de.royzer.fabrichg.util.tracker
import kotlinx.coroutines.job
import net.silkmc.silk.core.text.literalText
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.level.GameType
import net.silkmc.silk.core.logging.logError
import net.silkmc.silk.core.logging.logInfo

object ConnectEvents {
    init {
        ServerPlayConnectionEvents.JOIN.register { handler, sender, server ->
            val gamePhase = GamePhaseManager.currentPhase.phaseType
            val player = handler.player
            val uuid = player.uuid
            val hgPlayer = player.hgPlayer

            logInfo("${player.name.string} joint in ${gamePhase.name} mit Status ${hgPlayer.status}")


//            silkCoroutineScope.launch {
//                delay(200)
                player.showScoreboard()
//            }

            player.attributes.getInstance(Attributes.ATTACK_SPEED)?.baseValue = 550.0

            if (hgPlayer.kits.isEmpty())
                hgPlayer.kits.add(noneKit)

            when (gamePhase) {
                PhaseType.LOBBY -> {
                    player.removeAllEffects()
                    player.health = player.maxHealth
                    player.inventory.clearContent()
                    player.inventory.add(kitSelector)
                    if (player.hasPermissions(2)) {
                        player.inventory.setItem(8, gameSettingsItem)
                    }

                    player.foodData.foodLevel = 20
                    player.setGameMode(GameType.ADVENTURE)
//                    if (LobbyPhase.isStarting) player.freeze()
                    PlayerList.addOrGetPlayer(player.uuid, player.name.string)
                }

                PhaseType.INVINCIBILITY -> {
                    when (hgPlayer.status) {
                        HGPlayerStatus.DISCONNECTED -> {
                            combatloggedPlayers[uuid]?.job?.cancel()
                            player.hgPlayer.status = HGPlayerStatus.ALIVE
                        }

                        else -> {
                            player.removeAllEffects()
                            player.health = player.maxHealth
                            player.inventory.clearContent()
                            player.foodData.foodLevel = 20
                            player.setGameMode(GameType.SURVIVAL)
                            PlayerList.addOrGetPlayer(player.uuid, player.name.string)
                            player.inventory.add(tracker)
                            hgPlayer.kits.forEach { it.onEnable?.invoke(player.hgPlayer, it, player) }
                        }
                    }
                }

                PhaseType.INGAME -> {
                    onAnchorJoin(player)
                    when (player.hgPlayer.status) {
                        HGPlayerStatus.DISCONNECTED -> {
                            combatloggedPlayers[uuid]?.job?.cancel()
                            player.hgPlayer.status = HGPlayerStatus.ALIVE
                            logInfo("${player.name.string} ist wieder da")
                            hgPlayer.kits.forEach { it.onEnable?.invoke(hgPlayer, it, player) }
                        }

                        HGPlayerStatus.ALIVE -> {
                            logError("${player.name.string} joint als Alive")
                            player.hgPlayer.status = HGPlayerStatus.SPECTATOR
                            player.setGameMode(GameType.SPECTATOR)
                            player.removeAllEffects()
                        }

                        HGPlayerStatus.SPECTATOR -> {
                            player.hgPlayer.status = HGPlayerStatus.SPECTATOR
                            player.setGameMode(GameType.SPECTATOR)
                            player.removeAllEffects()
                        }
                    }
                }

                PhaseType.END -> {
                    player.hgPlayer.status = HGPlayerStatus.SPECTATOR
                    player.setGameMode(GameType.SPECTATOR)
                    player.removeAllEffects()
                }
            }


        }

        ServerPlayConnectionEvents.DISCONNECT.register { handler, server ->
            val gamePhase = GamePhaseManager.currentPhase.phaseType
            val player = handler.player
            val hgPlayer = player.hgPlayer
            val uuid = handler.player.uuid
            logInfo("${player.name.string} leaved in ${gamePhase.name} mit Status ${hgPlayer.status}")

            when (GamePhaseManager.currentPhase.phaseType) {
                PhaseType.LOBBY -> PlayerList.removePlayer(uuid)
                PhaseType.INVINCIBILITY -> {
                    player.startCombatlog()
                }

                PhaseType.INGAME -> {
                    if (player.hgPlayer.status == HGPlayerStatus.ALIVE) {
                        hgPlayer.kits.forEach { it.onDisable?.invoke(hgPlayer, it) }
                        val combatTracker = player.combatTracker
                        val lastCombatEntry = (combatTracker as CombatTrackerAcessor).entries.lastOrNull()
                        if (lastCombatEntry?.source?.entity is ServerPlayer) {
                            val killer = lastCombatEntry.source.entity as ServerPlayer
                            player.health = 0F
                            player.removeHGPlayer()
                            broadcastComponent(literalText {
                                text("${player.name.string} ist im Kampf gegen ${killer.name.string} geleaved")
                                color = 0xFFFF55
                            })
                            killer.hgPlayer.kills += 1
                            PlayerList.announceRemainingPlayers()
                        } else {
                            player.startCombatlog()
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