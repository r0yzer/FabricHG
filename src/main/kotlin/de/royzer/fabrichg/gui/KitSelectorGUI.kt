package de.royzer.fabrichg.gui

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.kits
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Items
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.item.setCustomName
import net.silkmc.silk.core.item.setLore
import net.silkmc.silk.core.kotlin.ticks
import net.silkmc.silk.core.text.literal
import net.silkmc.silk.core.text.literalText
import net.silkmc.silk.igui.*
import net.silkmc.silk.igui.observable.toGuiList

fun kitSelectorGUI(serverPlayerEntity: ServerPlayer, index: Int) = igui(GuiType.NINE_BY_FIVE, "Kit $index".literal, 1) {
    val hgPlayer = serverPlayerEntity.hgPlayer
    page(1) {
        placeholder(Slots.Border, Items.GRAY_STAINED_GLASS_PANE.guiIcon)

        val compound = compound(
            (2 sl 2) rectTo (4 sl 8),
            kits.sortedBy { it.name.first() }.filter { it.enabled }.toGuiList(),
            iconGenerator = { kit ->
                val defaultItemStack = itemStack(Items.BARRIER) { }

                (kit.kitSelectorItem ?: defaultItemStack).copy().apply {
                    setCustomName {
                        text(kit.name) {
                            color = if (hgPlayer.hasKit(kit)) 0x00FF00 else 0x00FFFF
                            strikethrough = false
                            bold = hgPlayer.hasKit(kit)
                            italic = false
                        }
                    }

                    if (kit.description.isNotBlank())
                        setLore(listOf(literalText(kit.description) {
                            color = if (hgPlayer.hasKit(kit)) 0xF1F2F1 else 0x4C3E4B
                            strikethrough = false
                            bold = false
                            italic = false
                        }))
                }
            },
            onClick = { _, kit ->
                hgPlayer.setKit(kit, index - 1)
                serverPlayerEntity.closeContainer()
            }
        )

        compoundScrollBackwards(5 sl 5, Items.RED_STAINED_GLASS_PANE.guiIcon, compound, speed = 3.ticks)
        compoundScrollForwards(1 sl 5, Items.GREEN_STAINED_GLASS_PANE.guiIcon, compound, speed = 3.ticks)
    }
}
