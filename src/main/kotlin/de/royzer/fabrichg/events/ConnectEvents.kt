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
import de.royzer.fabrichg.kit.kits.noneKit
import de.royzer.fabrichg.scoreboard.showScoreboard
import kotlinx.coroutines.job
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.item.setCustomName
import net.silkmc.silk.core.text.literalText
import net.silkmc.silk.core.text.sendText
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.Items
import net.minecraft.world.level.GameType

object ConnectEvents {
    init {
        ServerPlayConnectionEvents.JOIN.register { handler, sender, server ->
            val gamePhase = GamePhaseManager.currentPhase.phaseType
            val player = handler.player
            val uuid = player.uuid
            val hgPlayer = player.hgPlayer


            player.attributes.getInstance(Attributes.ATTACK_SPEED)?.baseValue = 100.0

            if (hgPlayer.kits.isEmpty())
                hgPlayer.kits.add(noneKit)

            when (gamePhase) {
                PhaseType.LOBBY -> {
                    player.removeAllEffects()
                    player.health = player.maxHealth
                    player.inventory.clearContent()
                    player.inventory.add(itemStack(Items.CHEST) {
                        setCustomName { text("Kit Selector") }
                    })
                    player.foodData.foodLevel = 20
                    player.setGameMode(GameType.ADVENTURE)
                    if (LobbyPhase.isStarting) player.freeze()
                    PlayerList.addOrGetPlayer(player.uuid, player.name.string)
                }
                PhaseType.INVINCIBILITY -> {
                    when (hgPlayer.status) {
                        HGPlayerStatus.DISCONNECTED -> {
                            combatloggedPlayers[uuid]?.job?.cancel()
                            player.hgPlayer.status = HGPlayerStatus.ALIVE
                        }
                        else -> {
                            PlayerList.addOrGetPlayer(player.uuid, player.name.string)
                        }
                    }
                }
                PhaseType.INGAME -> {
                    when (player.hgPlayer.status) {
                        HGPlayerStatus.DISCONNECTED -> {
                            combatloggedPlayers[uuid]?.job?.cancel()
                            player.hgPlayer.status = HGPlayerStatus.ALIVE
                        }
                        else -> {
                            player.hgPlayer.status = HGPlayerStatus.SPECTATOR
                            player.setGameMode(GameType.SPECTATOR)
                            player.removeAllEffects()
                            player.sendText(literalText("nunja gamne schon start") { })
                        }
                    }
                }
                PhaseType.END -> {
                    player.hgPlayer.status = HGPlayerStatus.SPECTATOR
                    player.setGameMode(GameType.SPECTATOR)
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
                        if (false){//handler.player.combatTracker.lastEntry?.attacker is ServerPlayer) {
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