package de.royzer.fabrichg.commands

import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.data.hgplayer.HGPlayerStatus
import de.royzer.fabrichg.game.PlayerList
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.text.literalText

val listCommand = command("list") {
    runs {
        val text = literalText {
            text("Alle Spieler: ") { color = TEXT_GRAY }
            PlayerList.players.values.forEach { hgPlayer ->
                newLine()
                text(hgPlayer.name + " ") {
                    color = hgPlayer.status.statusColor
                }
                text("Verbleibende Offline Zeit: ${hgPlayer.offlineTime}s") { color = TEXT_GRAY }
            }
        }
        source.playerOrException.sendSystemMessage(text)
    }
}