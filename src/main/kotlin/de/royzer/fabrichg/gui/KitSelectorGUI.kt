package de.royzer.fabrichg.gui

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.kit.kits
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.item.setCustomName
import net.silkmc.silk.core.text.literal
import net.silkmc.silk.igui.*
import net.silkmc.silk.igui.observable.toGuiList
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Items

fun kitSelectorGUI(serverPlayerEntity: ServerPlayer) = igui(GuiType.NINE_BY_FIVE, "Kits".literal, 1) {
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
                if (GamePhaseManager.isIngame) {
                    kit.onEnable?.invoke(hgPlayer, kit)
                    kit.kitItems.forEach { serverPlayerEntity.inventory.add(it.itemStack.copy()) }
                }
                serverPlayerEntity.closeContainer()
            }
        )

        compoundScrollBackwards(3 sl 1, Items.RED_STAINED_GLASS_PANE.guiIcon, compound)
        compoundScrollForwards(3 sl 9, Items.GREEN_STAINED_GLASS_PANE.guiIcon, compound)
    }
}
