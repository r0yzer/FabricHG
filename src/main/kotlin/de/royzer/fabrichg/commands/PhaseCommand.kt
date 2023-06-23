package de.royzer.fabrichg.commands

import de.royzer.fabrichg.game.PlayerList
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.text.literalText


val phaseCommand = command("phase") {
    runs {
        val text = literalText {
            PlayerList.players.values.forEach { hgPlayer ->
                text("${hgPlayer.name}, ") {
                    color = hgPlayer.status.statusColor
                }
            }
        }
        source.playerOrException.sendSystemMessage(text)
    }
}