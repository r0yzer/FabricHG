package de.royzer.fabrichg.gui

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.data.hgplayer.HGPlayerStatus
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.PlayerList
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Items
import net.silkmc.silk.core.item.setCustomName
import net.silkmc.silk.core.item.setLore
import net.silkmc.silk.core.item.setSkullPlayer
import net.silkmc.silk.core.text.literal
import net.silkmc.silk.core.text.literalText
import net.silkmc.silk.igui.*
import net.silkmc.silk.igui.observable.toGuiList

fun gameOverviewGUI(serverPlayer: ServerPlayer): Gui {
    return igui(GuiType.NINE_BY_FIVE, "Game overview".literal, 1) {
        val permitted = serverPlayer.hasPermissions(4) || serverPlayer.hgPlayer.status == HGPlayerStatus.SPECTATOR
        page(1) {
            placeholder(Slots.Border, Items.GRAY_STAINED_GLASS_PANE.guiIcon)

            val compound = compound(
                (2 sl 2) rectTo (4 sl 8),
                PlayerList.alivePlayers.sortedBy { it.kills }.toGuiList(),
                iconGenerator = { hgPlayer ->
                    val skull = Items.PLAYER_HEAD.defaultInstance
                    hgPlayer.serverPlayer?.let { skull.setSkullPlayer(it) }
                    skull.setCustomName {
                        text(if (hgPlayer.isBot) "Bot " else "") {
                            color = 0xFF0000
                        }
                        text(hgPlayer.name) {
                            color = hgPlayer.status.statusColor
                        }
                        text(" Kills: " + hgPlayer.kills.toString()) { color = TEXT_BLUE }
                    }
                    if (permitted) {
                        skull.setLore(listOf(
                            literalText {
                                text(if (hgPlayer.kits.size == 1) "Kit: " else "Kits: ")
                                text(hgPlayer.kits.joinToString()) {
                                    color = TEXT_BLUE
                                    bold = true
                                }
                                color = TEXT_GRAY
                                italic = false
                            }
                        ))
                    }

                    skull
                },
                onClick = { event, element ->
                }
            )

            compoundScrollBackwards(5 sl 5, Items.RED_STAINED_GLASS_PANE.guiIcon, compound)
            compoundScrollForwards(1 sl 5, Items.GREEN_STAINED_GLASS_PANE.guiIcon, compound)
        }
    }
}