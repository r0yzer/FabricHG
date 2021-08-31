package de.royzer.fabrichg.commands

import de.royzer.fabrichg.data.hgplayer.HGPlayerStatus
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.combatlog.combatloggedPlayers
import net.axay.fabrik.commands.command
import net.axay.fabrik.commands.simpleExecutes
import net.axay.fabrik.core.text.literalText

val listCommand = command("list") {
    simpleExecutes {
        val text = literalText text@{
            PlayerList.players.forEach { data ->
                val hgPlayer = data.value

                when (hgPlayer.status) {
                    HGPlayerStatus.ALIVE -> text("${hgPlayer.name}, ") {
                        color = 0x00FF32
                    }
                    HGPlayerStatus.DEAD -> text("${hgPlayer.name}, ") {
                        color = 0xFF0000
                    }
                    HGPlayerStatus.SPECTATOR -> text("${hgPlayer.name}, ") {
                        color = 0xE9E9E9
                    }
                    else -> text("${hgPlayer.name}, ") {
                        color = 0xFF4CC0
                    }
                }

            }
        }
        this.source.player.sendMessage(text, false)
    }
}