package de.royzer.fabrichg.gui

import de.royzer.fabrichg.game.PlayerList
import net.silkmc.silk.core.item.setCustomName
import net.silkmc.silk.core.text.literal
import net.silkmc.silk.igui.*
import net.silkmc.silk.igui.observable.toGuiList
import net.minecraft.world.item.Items

val gameOverviewGUI = igui(GuiType.NINE_BY_FIVE, "Spielübersicht".literal, 1) {
    page(1) {
        placeholder(Slots.Border, Items.GRAY_STAINED_GLASS_PANE.guiIcon)

        val compound = compound(
            (2 sl 2) rectTo (4 sl 8),
            PlayerList.alivePlayers.toGuiList(),
            iconGenerator = {
                Items.PLAYER_HEAD.defaultInstance.setCustomName(it.name.literal.toString())
            },
            onClick = { event, element ->

            }
        )

        compoundScrollBackwards(3 sl 1, Items.RED_STAINED_GLASS_PANE.guiIcon, compound)
        compoundScrollForwards(3 sl 9, Items.GREEN_STAINED_GLASS_PANE.guiIcon, compound)
    }
}