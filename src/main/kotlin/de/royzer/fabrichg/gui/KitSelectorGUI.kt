package de.royzer.fabrichg.gui

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.kits
import net.axay.fabrik.core.item.itemStack
import net.axay.fabrik.core.item.setCustomName
import net.axay.fabrik.core.text.literal
import net.axay.fabrik.igui.*
import net.axay.fabrik.igui.observable.toGuiList
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity

fun kitSelectorGUI(serverPlayerEntity: ServerPlayerEntity) = igui(GuiType.NINE_BY_FIVE, "Kits".literal, 1) {
    val hgPlayer = serverPlayerEntity.hgPlayer
    page(1) {
        placeholder(Slots.Border, Items.GRAY_STAINED_GLASS_PANE.guiIcon)

        val compound = compound(
            (2 sl 2) rectTo (4 sl 8),
            kits.sortedBy { it.name.first() }.toGuiList(),
            iconGenerator = { kit ->
                itemStack(kit.kitSelectorItem ?: Items.BARRIER) {
                    setCustomName {
                        text(kit.name) {
                            color = if (hgPlayer.hasKit(kit)) 0x00FF00 else 0x00FFFF
                            strikethrough = false
                            bold = hgPlayer.hasKit(kit)
                            italic = false
                        }
                    }
                }
            },
            onClick = { _, kit ->
                hgPlayer.kits[0] = kit
                serverPlayerEntity.closeHandledScreen()
            }
        )

        compoundScrollBackwards(3 sl 1, Items.RED_STAINED_GLASS_PANE.guiIcon, compound)
        compoundScrollForwards(3 sl 9, Items.GREEN_STAINED_GLASS_PANE.guiIcon, compound)
    }
}
