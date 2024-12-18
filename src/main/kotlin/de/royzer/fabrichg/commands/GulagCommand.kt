package de.royzer.fabrichg.commands

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.phase.phases.playerInfoText
import de.royzer.fabrichg.gulag.GulagManager
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.HoverEvent
import net.minecraft.server.level.ServerPlayer
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.text.literalText
import net.silkmc.silk.core.text.sendText

val gulagCommand = command("gulag") {
    literal("info") {
        runs {
            if (GulagManager.empty) {
                source.playerOrException.sendText(literalText("Das Gulag ist leer") {
                    color = TEXT_GRAY
                })

                return@runs
            }
            if (!GulagManager.gulagEnabled) {
                source.playerOrException.sendText(literalText("Das Gulag ist ausgeschalten") { // sabayern
                    color = TEXT_GRAY
                })

                return@runs
            }

            val text = literalText {
                text("Gulag Info\n") { color = TEXT_GRAY }
                text("Open: ") { color = TEXT_GRAY }
                text("${GulagManager.open}\n") { color = TEXT_BLUE }

                if (GulagManager.fighting.isNotEmpty()) {
                    text("Fighting:\n") { color = TEXT_GRAY }

                    GulagManager.fighting.forEach { player ->
                        text(" - ") { color = TEXT_GRAY }

                        text("${player.name}\n") {
                            color = TEXT_BLUE
                            underline = true

                            hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, playerInfoText(player))
                        }
                    }

                    newLine()
                }

                if (!GulagManager.gulagQueue.isEmpty()) {
                    text("In Queue:\n") { color = TEXT_GRAY }

                    GulagManager.gulagQueue.forEachIndexed { index, player ->
                        text(" ${index+1}. ") { color = TEXT_GRAY }

                        text("${player.name}\n") {
                            color = TEXT_BLUE

                            hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, playerInfoText(player))
                        }
                    }
                }
            }

            source.playerOrException.sendText(text)
        }
    }

    literal("close") {
        requiresPermissionLevel(4)

        runs {
            GulagManager.close()
        }
    }

    literal("send") {
        requiresPermissionLevel(4)

        argument("player", EntityArgument.player()) { player ->
            runs {
                val serverPlayer = player(this).findPlayers(source).first() ?: return@runs

                val hgPlayer = serverPlayer.hgPlayer

                hgPlayer.addToGulag(source.player ?: return@runs)
            }
        }

        runs {
            val serverPlayer = source.player ?: return@runs

            val hgPlayer = serverPlayer.hgPlayer

            hgPlayer.addToGulag(serverPlayer)
        }
    }
}

private fun HGPlayer.addToGulag(source: ServerPlayer) {
    if (!GulagManager.open) {
        source.sendSystemMessage(literalText("Das Gualg ist schon zu") {
            color = TEXT_GRAY
        })

        return
    }

    if (GulagManager.isInGulag(this)) {
        source.sendSystemMessage(literalText("$name ist schon im Gulag") {
            color = TEXT_GRAY
        })

        return
    }

    GulagManager.sendToGulag(this)
}