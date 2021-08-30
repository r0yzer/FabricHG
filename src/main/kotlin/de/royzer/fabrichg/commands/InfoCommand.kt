package de.royzer.fabrichg.commands

import de.royzer.fabrichg.game.GamePhaseManager
import net.axay.fabrik.commands.command
import net.axay.fabrik.commands.simpleExecutes
import net.axay.fabrik.core.text.literalText

val infoCommand = command("info") {
    simpleExecutes {
        this.source.player.sendMessage(
            literalText {
                text("Phase: ${GamePhaseManager.currentPhaseType.name}") { color = 0xFF006E }
            }, false
        )
    }
}