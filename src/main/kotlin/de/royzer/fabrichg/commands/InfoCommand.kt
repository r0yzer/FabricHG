package de.royzer.fabrichg.commands

import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.GamePhaseManager
import net.silkmc.silk.core.text.literalText
import net.silkmc.silk.core.text.sendText
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.Silk

val infoCommand = command("info") {
    runs {
        val hgPlayer = source.playerOrException.hgPlayer
        source.playerOrException.sendText(
            literalText {
                text("Phase: ${GamePhaseManager.currentPhaseType.name}") { color = TEXT_GRAY }
                newLine()
                text("Status: ${hgPlayer.status}") { color = hgPlayer.status.statusColor }
                newLine()
                text("Kills: ${hgPlayer.kills}") { color = TEXT_GRAY }
                newLine()
                text("Kit(s): ${hgPlayer.kits.joinToString(",", postfix = "")}") { color = TEXT_GRAY }
            }
        )
    }
}