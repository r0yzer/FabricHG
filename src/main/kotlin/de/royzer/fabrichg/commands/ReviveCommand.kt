package de.royzer.fabrichg.commands

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.data.hgplayer.HGPlayerStatus
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.broadcastComponent
import net.minecraft.commands.arguments.EntityArgument
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.text.literalText
import net.silkmc.silk.core.text.sendText

val reviveCommand = command("revive") {
    requiresPermissionLevel(4)
    argument("player", EntityArgument.player()) { player ->
        runs {
            val serverPlayer = player(this).findPlayers(this.source).first() ?: return@runs
            val hgPlayer = serverPlayer.hgPlayer
            if (hgPlayer.status == HGPlayerStatus.ALIVE) {
                this.source.player?.sendText {
                    text("This player is still alive") {
                        color = TEXT_GRAY
                    }
                }
                return@runs
            }
            hgPlayer.status = HGPlayerStatus.ALIVE
            hgPlayer.kits.forEach {
                it.onEnable?.invoke(hgPlayer, it, serverPlayer)
            }
            serverPlayer.health = 20f
            serverPlayer.foodData.foodLevel = 20

            broadcastComponent(literalText {
                text(serverPlayer.name.string) {
                    color = TEXT_BLUE
                    bold = true
                }
                text(" was revived by ") {
                    color = TEXT_GRAY
                }
                text(source.player?.name?.string.toString()) {
                    color = TEXT_BLUE
                    bold = true
                }
            })


        }

    }
}