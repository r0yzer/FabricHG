package de.royzer.fabrichg.commands

import de.royzer.fabrichg.game.PlayerList
import net.axay.fabrik.commands.command
import net.axay.fabrik.core.text.literalText
import net.minecraft.Util

val listCommand = command("list") {
    runs {
        val text = literalText {
            PlayerList.players.values.forEach { hgPlayer ->
                text("${hgPlayer.name}, ") {
                    color = hgPlayer.status.statusColor
                }
            }
        }
        source.playerOrException.sendMessage(text, Util.NIL_UUID)
    }
}