package de.royzer.fabrichg.commands

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.GamePhaseManager
import net.axay.fabrik.commands.command
import net.axay.fabrik.commands.simpleExecutes
import net.axay.fabrik.core.text.literalText

val infoCommand = command("info") {
    simpleExecutes {
        this.source.player.sendMessage(
            literalText {
                text("Phase: ${GamePhaseManager.currentPhaseType.name}") { color = 0xFF006E }
                newLine()
                text("Status: ${source.player.hgPlayer.status}") { color = 0xFF0802 }
                newLine()
                text("Kills: ${source.player.hgPlayer.kills}") { color = 0x143CFF }
            }, false
        )
    }
}