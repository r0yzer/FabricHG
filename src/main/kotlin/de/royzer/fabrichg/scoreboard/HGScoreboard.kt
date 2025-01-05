package de.royzer.fabrichg.scoreboard

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.TEXT_YELLOW
import de.royzer.fabrichg.feast.Feast
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.game.phase.phases.EndPhase
import de.royzer.fabrichg.game.phase.phases.IngamePhase
import de.royzer.fabrichg.game.phase.phases.InvincibilityPhase
import de.royzer.fabrichg.game.phase.phases.LobbyPhase
import de.royzer.fabrichg.game.teams.hgTeam
import de.royzer.fabrichg.game.teams.isInTeam
import de.royzer.fabrichg.settings.ConfigManager
import kotlinx.datetime.Clock
import net.minecraft.server.level.ServerPlayer
import net.silkmc.silk.core.kotlin.ticks
import net.silkmc.silk.core.task.mcCoroutineTask
import net.silkmc.silk.core.text.literal
import net.silkmc.silk.core.text.literalText
import net.silkmc.silk.game.sideboard.sideboard
import kotlin.time.Duration.Companion.milliseconds

fun ServerPlayer.showScoreboard() {
    val hgPlayer = PlayerList.addOrGetPlayer(uuid, name.string)
    val board = sideboard(
        literalText("HGLabor") { color = 0xFF00C8; bold = true }
    ) {
        updatingLine(100.milliseconds) {
                                                                    // koks ?
            when (GamePhaseManager.currentPhaseType) {              // rr keine ahnung warum man hier 1 dazurechnen muss
                PhaseType.LOBBY -> literalText("Start in: ${(1 + LobbyPhase.maxPhaseTime - GamePhaseManager.timer.get()).formattedTime}") { color = TEXT_YELLOW }
                PhaseType.END -> literalText("Zeit: ${(GamePhaseManager.currentPhase as EndPhase).endTime.formattedTime}") { color = TEXT_YELLOW }
                PhaseType.INVINCIBILITY -> literalText {
                    text("Invincibility: ") { color = TEXT_GRAY }
                    text((InvincibilityPhase.maxPhaseTime - GamePhaseManager.timer.get()).formattedTime) { color = TEXT_YELLOW }
                }
                else -> literalText("Zeit: ${(GamePhaseManager.timer.get()).formattedTime}") { color = TEXT_YELLOW }
            }
        }
        emptyLine()


        updatingLine(1000.milliseconds) {
            literalText {
                text(if (ConfigManager.gameSettings.kitAmount <= 1) "Kit: " else "Kits: ") {
                    color = TEXT_GRAY
                    strikethrough = hgPlayer.kitsDisabled
                }
                text(hgPlayer.kits.joinToString { it.name }) {
                    color = TEXT_BLUE
                    strikethrough = hgPlayer.kitsDisabled
                }
            }
        }

        updatingLine(1000.milliseconds) {
            literalText {
                text("Kills: ") { color = TEXT_GRAY }
                text(hgPlayer.kills.toString()) { color = TEXT_BLUE }
            }
        }

        updatingLine(100.milliseconds) {
            val timeUntilFeast = (Feast.feastTimestamp?.epochSecond ?: 0) - Clock.System.now().epochSeconds
            val timeUntilPit = IngamePhase.pitStartTime - GamePhaseManager.timer.get()
            val timeUntilEnd = IngamePhase.maxPhaseTime - GamePhaseManager.timer.get()

            val showThreshold = 60 * 5

            literalText {
                if (timeUntilEnd in 0..showThreshold) {
                    text("End in: ") { color = TEXT_GRAY }
                    text(timeUntilEnd.formattedTime) { color = TEXT_YELLOW }
                } else if (timeUntilPit in 0..showThreshold && ConfigManager.gameSettings.pitEnabled) {
                    text("Pit in: ") { color = TEXT_GRAY }
                    text(timeUntilPit.formattedTime) { color = TEXT_YELLOW }
                } else if (timeUntilFeast in 0..showThreshold) {
                    text("Feast in: ") { color = TEXT_GRAY }
                    text(timeUntilFeast.toInt().formattedTime) { color = TEXT_YELLOW }
                }
            }
        }

        if (ConfigManager.gameSettings.teamsEnabled) emptyLine()

        updatingLine(100.milliseconds) {
            if (!hgPlayer.isInTeam) return@updatingLine "".literal

            literalText {
                text("Team: ") { color = TEXT_GRAY}
                text(hgPlayer.hgTeam?.let { "${it.name} (${it.hgPlayers.size})" } ?: "in team aber kein team?") { color = TEXT_BLUE }
            }
        }

        line(literalText {
            text("Spieler:") { color = TEXT_GRAY }
        })
        updatingLine(1000.milliseconds) {
            literalText {
                text("  ${PlayerList.alivePlayers.size}/${PlayerList.maxPlayers}") {
                    color = TEXT_BLUE
                }
            }

        }

        updatingLine(1.ticks) {
            hgPlayer.updateScoreboard()
            val kitInfos = hgPlayer.kitInfos
            if (kitInfos.isEmpty()) return@updatingLine "".literal // keine infos -> nix
            val infoIndex = (server.tickCount / 60) % kitInfos.size
            kitInfos.getOrNull(infoIndex) ?: "null kit info?".literal
        }
        emptyLine()

        updatingLine(1000.milliseconds) {
            literalText(hgPlayer.status.toString()) {
                color = hgPlayer.status.statusColor
            }
        }
    }

    mcCoroutineTask(delay=10.ticks) {
        hgPlayer.updateScoreboard()
        board.displayToPlayer(this@showScoreboard)
    }
}

val Int.formattedTime: String
    get() {
        val m = this / 60
        val s = if (this % 60 >= 10) this % 60 else "0${this % 60}"
        return "$m:$s"
    }