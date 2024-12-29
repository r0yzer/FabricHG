package de.royzer.fabrichg.scoreboard

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.TEXT_YELLOW
import de.royzer.fabrichg.data.hgplayer.HGPlayerStatus
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.game.phase.phases.EndPhase
import de.royzer.fabrichg.game.phase.phases.LobbyPhase
import de.royzer.fabrichg.settings.ConfigManager
import net.minecraft.server.level.ServerPlayer
import net.silkmc.silk.core.kotlin.ticks
import net.silkmc.silk.core.task.mcCoroutineTask
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

        emptyLine()
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
        emptyLine()
        updatingLine(1000.milliseconds) {
            literalText(hgPlayer.status.toString()) {
                color = hgPlayer.status.statusColor
            }
        }
    }

    mcCoroutineTask(delay=10.ticks) {
        board.displayToPlayer(this@showScoreboard)
    }
}

val Int.formattedTime: String
    get() {
        val m = this / 60
        val s = if (this % 60 >= 10) this % 60 else "0${this % 60}"
        return "$m:$s"
    }