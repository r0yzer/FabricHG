package de.royzer.fabrichg.commands

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.game.phase.phases.playerInfoText
import de.royzer.fabrichg.gulag.GulagManager
import net.minecraft.network.chat.HoverEvent
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

                if (!GulagManager.fighting.isEmpty()) {
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
}