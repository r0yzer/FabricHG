package de.royzer.fabrichg.command.commands

import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.command.hgCommand
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.gui.gameOverviewGUI
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.text.literalText
import net.silkmc.silk.core.text.sendText
import net.silkmc.silk.igui.openGui

val gameCommand = hgCommand("game") {
    runs {
        val player = source.player ?: return@runs
        player.openGui(gameOverviewGUI(player), 1)
    }
    literal("info") runs {
        val hgPlayer = source.player?.hgPlayer ?: return@runs
        source.player!!.sendText(
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