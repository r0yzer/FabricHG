package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.cooldown.activateCooldown
import de.royzer.fabrichg.kit.kit
import net.minecraft.world.item.Items
import net.silkmc.silk.core.logging.logInfo

val mirrorKit = kit("Mirror") {
    kitSelectorItem = Items.GLASS_PANE.defaultInstance

    usableInInvincibility = false
    cooldown = 35.0
    description = "mirror your enemies"

    kitItem {
        itemStack = kitSelectorItem

        onClickAtPlayer { hgPlayer, kit, clickedPlayer, hand ->
            if (clickedPlayer.hgPlayer.isNeo) return@onClickAtPlayer
            val oldInventory = clickedPlayer.inventory.items
            val newItems = MutableList(oldInventory.size) {Items.AIR.defaultInstance}
            // ?
            oldInventory.forEachIndexed { index, itemStack ->
                when(index) {
                    in 0..8 -> newItems[8 - index] = itemStack.copy()
                    in 9..17 -> newItems[17 + 9 - index] = itemStack.copy()
                    in 18..26 -> newItems[26 + 18 - index] = itemStack.copy()
                    in 27..35 -> newItems[35 + 27 - index] = itemStack.copy()
                }
            }
            newItems.forEachIndexed { index, itemStack ->
                clickedPlayer.inventory.setItem(index, itemStack.copy())
            }
            hgPlayer.activateCooldown(kit)
        }
    }
}