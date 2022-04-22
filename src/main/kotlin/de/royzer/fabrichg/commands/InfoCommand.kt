package de.royzer.fabrichg.commands

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.GamePhaseManager
import net.axay.fabrik.commands.command
import net.axay.fabrik.core.text.literalText
import net.axay.fabrik.core.text.sendText

val infoCommand = command("info") {
    runs {
        val hgPlayer = source.playerOrException.hgPlayer
        source.playerOrException.sendText(
            literalText {
                text("Phase: ${GamePhaseManager.currentPhaseType.name}") { color = 0xFF006E }
                newLine()
                text("Status: ${hgPlayer.status}") { color = hgPlayer.status.statusColor }
                newLine()
                text("Kills: ${hgPlayer.kills}") { color = 0x143CFF }
            }
        )
    }
}