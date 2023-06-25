package de.royzer.fabrichg.scoreboard

import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.game.phase.phases.EndPhase
import de.royzer.fabrichg.game.phase.phases.LobbyPhase
import net.minecraft.network.chat.MutableComponent
import net.silkmc.silk.core.text.literalText
import net.silkmc.silk.game.sideboard.showSideboard
import net.silkmc.silk.game.sideboard.sideboard
import net.minecraft.server.level.ServerPlayer
import net.silkmc.silk.core.text.LiteralTextBuilder
import kotlin.time.Duration.Companion.milliseconds

fun ServerPlayer.showScoreboard() {
    val hgPlayer = PlayerList.addOrGetPlayer(uuid, name.string)
    sideboard(
        literalText("Fabric HG") { color = 0xFF00C8 }
    ) {
        updatingLine(1000.milliseconds) {
            when (GamePhaseManager.currentPhaseType) {
                PhaseType.LOBBY -> literalText("Start in: ${(LobbyPhase.maxPhaseTime - GamePhaseManager.timer.get()).formattedTime}")
                PhaseType.END -> literalText("Zeit: ${(GamePhaseManager.currentPhase as EndPhase).endTime.formattedTime}")
                else -> literalText("Zeit: ${(GamePhaseManager.timer.get()).formattedTime}")
            }
        }
        emptyLine()
        updatingLine(1000.milliseconds) { literalText("Kills: ${hgPlayer.kills}") { color = 0x0032FF } }
        updatingLine(1000.milliseconds) {
            literalText("Kit(s): ${hgPlayer.kits.joinToString { it.name }}") {
                color = 0x00FFFF
                strikethrough = hgPlayer.kitsDisabled
            }
        }
        line(literalText("") { })
        line(literalText("Spieler:") {
            color = 0xFF0096
        })
        updatingLine(1000.milliseconds) {
            literalText("${PlayerList.alivePlayers.size}/${PlayerList.maxPlayers}") {
                color = 0x0032FF
            }
        }
        emptyLine()
        updatingLine(1000.milliseconds) {
            literalText(hgPlayer.status.toString()) {
                color = hgPlayer.status.statusColor
            }
        }
    }
        .displayToPlayer(this)
}

val Int.formattedTime: String
    get() {
        val m = this / 60
        val s = if (this % 60 >= 10) this % 60 else "0${this % 60}"
        return "$m:$s"
    }