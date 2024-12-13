package de.royzer.fabrichg.events

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.bots.player.FakeServerPlayer
import de.royzer.fabrichg.data.hgplayer.HGPlayerStatus
import de.royzer.fabrichg.data.hgplayer.giveKitSelectors
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.broadcastComponent
import de.royzer.fabrichg.game.combatlog.combatloggedPlayers
import de.royzer.fabrichg.game.combatlog.startCombatlog
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.game.removeHGPlayer
import de.royzer.fabrichg.kit.kits.onAnchorJoin
import de.royzer.fabrichg.mixins.world.CombatTrackerAcessor
import de.royzer.fabrichg.scoreboard.showScoreboard
import de.royzer.fabrichg.stats.Database
import de.royzer.fabrichg.util.gameSettingsItem
import de.royzer.fabrichg.util.tracker
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.Items
import net.minecraft.world.level.GameType
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.logging.logError
import net.silkmc.silk.core.logging.logInfo
import net.silkmc.silk.core.task.mcCoroutineScope
import net.silkmc.silk.core.text.literalText
import net.silkmc.silk.core.text.sendText

object ConnectEvents {
    init {
        ServerPlayConnectionEvents.JOIN.register { handler, sender, server ->
            val gamePhase = GamePhaseManager.currentPhase.phaseType
            val player = handler.player

            if (player is FakeServerPlayer) {
                return@register
            }

            val uuid = player.uuid
            val hgPlayer = player.hgPlayer

            logInfo("${player.name.string} joint in ${gamePhase.name} mit Status ${hgPlayer.status}")


            mcCoroutineScope.launch {
                delay(200)
                player.showScoreboard()
            }
            Database.initPlayer(player)
            player.attributes.getInstance(Attributes.ATTACK_SPEED)?.baseValue = 550.0

            hgPlayer.fillKits()

            when (gamePhase) {
                PhaseType.LOBBY -> {
                    player.removeAllEffects()
                    player.health = player.maxHealth
                    player.inventory.clearContent()
                    player.giveKitSelectors()
                    if (player.hasPermissions(1)) {
                        player.inventory.setItem(8, gameSettingsItem)
                    }

                    player.foodData.foodLevel = 20
                    player.setGameMode(GameType.ADVENTURE)
                    PlayerList.addOrGetPlayer(player.uuid, player.name.string)
                }

                PhaseType.INVINCIBILITY, PhaseType.FFA -> {
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

                            val soup = itemStack(Items.MUSHROOM_STEW) { }

                            val recraft = 32
                            val bowls = itemStack(Items.BOWL, recraft) { }
                            val brown = itemStack(Items.BROWN_MUSHROOM, recraft) { }
                            val red = itemStack(Items.RED_MUSHROOM, recraft) { }

                            repeat(36) {
                                player.inventory.add(soup.copy())
                            }

                            player.inventory.setItem(13, bowls.copy())
                            player.inventory.setItem(14, brown.copy())
                            player.inventory.setItem(15, red.copy())
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
                            player.sendText {
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

                PhaseType.INGAME, PhaseType.FFA -> {
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