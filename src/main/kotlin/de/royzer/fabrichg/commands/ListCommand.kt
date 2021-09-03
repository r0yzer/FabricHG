package de.royzer.fabrichg.commands

import de.royzer.fabrichg.data.hgplayer.HGPlayerStatus
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.combatlog.combatloggedPlayers
import net.axay.fabrik.commands.command
import net.axay.fabrik.commands.internal.SimpleCommandContext
import net.axay.fabrik.commands.runs
import net.axay.fabrik.commands.simpleExecutes
import net.axay.fabrik.core.text.literalText
import net.minecraft.server.command.ServerCommandSource

val listCommand = command("list") {
    runs {
        val text = literalText {
            PlayerList.players.values.forEach { hgPlayer ->
                text("${hgPlayer.name}, ") {
                    color = hgPlayer.status.statusColor
                }
            }
        }
        source.player.sendMessage(text, false)
    }
}