package de.royzer.fabrichg.commands

import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.combatlog.combatloggedPlayers
import net.axay.fabrik.commands.command
import net.axay.fabrik.commands.simpleExecutes
import net.axay.fabrik.core.text.literalText

val listCommand = command("list") {
    simpleExecutes {
        val text = literalText text@{
            PlayerList.players.forEach { uuid ->
                val serverPlayerEntity = GamePhaseManager.server.playerManager.getPlayer(uuid)

                if (serverPlayerEntity != null) {
                    text(serverPlayerEntity.name.string) {
                        color = 0x00FF32
                    }
                } else {
                    val offlinePlayer = combatloggedPlayers[uuid]
                    text(offlinePlayer?.name.orEmpty()) {
                        color = 0xFF0000
                    }
                }
            }
        }
        this.source.player.sendMessage(text, false)
    }
}