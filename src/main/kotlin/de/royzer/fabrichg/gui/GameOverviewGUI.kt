package de.royzer.fabrichg.gui

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.PlayerList
import net.minecraft.server.level.ServerPlayer
import net.silkmc.silk.core.item.setCustomName
import net.silkmc.silk.core.text.literal
import net.silkmc.silk.igui.*
import net.silkmc.silk.igui.observable.toGuiList
import net.minecraft.world.item.Items

fun gameOverviewGUI(serverPlayer: ServerPlayer): Gui {
    val hgPlayer = serverPlayer.hgPlayer
    return igui(GuiType.NINE_BY_FIVE, "Spielübersicht".literal, 1) {
        page(1) {
            placeholder(Slots.Border, Items.GRAY_STAINED_GLASS_PANE.guiIcon)

            val compound = compound(
                (2 sl 2) rectTo (4 sl 8),
                PlayerList.alivePlayers.toGuiList(),
//            List(5) {it}.toGuiList(),
                iconGenerator = { hgPlayer ->
                    Items.PLAYER_HEAD.defaultInstance.setCustomName(hgPlayer.name) {
                        newLine()
                        text("Kills: " + hgPlayer.kills.toString()) { color = TEXT_BLUE }
                    }
                },
                onClick = { event, element ->

                }
            )

            compoundScrollBackwards(3 sl 1, Items.RED_STAINED_GLASS_PANE.guiIcon, compound)
            compoundScrollForwards(3 sl 9, Items.GREEN_STAINED_GLASS_PANE.guiIcon, compound)
        }
    }
}