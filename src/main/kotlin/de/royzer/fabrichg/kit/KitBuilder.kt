package de.royzer.fabrichg.kit

import net.minecraft.item.ItemStack
import net.minecraft.item.Items

class KitBuilder(val kit: Kit) {
    var kitSelectorItem: ItemStack = ItemStack(Items.AIR)
        set(value) {
            kit.kitSelectorItem = value.item
            field = value
        }

    fun addKitItem(kitItem: KitItem) {
        kit.kitItems.add(kitItem)
    }
    fun addKitItem(itemStack: ItemStack, droppable: Boolean) {
        kit.kitItems.add(KitItem(itemStack, droppable))
    }
}