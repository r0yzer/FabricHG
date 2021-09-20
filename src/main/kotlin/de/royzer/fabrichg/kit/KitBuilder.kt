package de.royzer.fabrichg.kit

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import net.axay.fabrik.core.item.setLore
import net.axay.fabrik.core.text.literalText
import net.minecraft.item.ItemStack
import net.minecraft.item.Items

class KitBuilder(val kit: Kit) {
    var kitSelectorItem: ItemStack = ItemStack(Items.AIR)
        get() = kit.kitSelectorItem?.defaultStack ?: ItemStack(Items.AIR)
        set(value) {
            kit.kitSelectorItem = value.item
            field = value
        }

    fun addKitItem(kitItem: KitItem) {
        kitItem.itemStack.setLore(listOf(literalText("Kititem")))
        kit.kitItems.add(kitItem)
    }

    fun addKitItem(itemStack: ItemStack, droppable: Boolean, clickAction: ((HGPlayer, Kit) -> Unit)? = null) {
        addKitItem(KitItem(itemStack, droppable, clickAction = clickAction))
    }

    fun kitItem(itemStack: ItemStack = Items.BARRIER.defaultStack, builder: KitItemBuilder.() -> Unit) {
        val kitItem = KitItem(itemStack)
        kitItem.apply { KitItemBuilder(kitItem).apply(builder) }
        addKitItem(kitItem)
    }

    fun onDisable(action: (hgPlayer: HGPlayer, kit: Kit) -> Unit) {
        kit.onDisable = action
    }
    fun onEnable(action: (hgPlayer: HGPlayer, kit: Kit) -> Unit) {
        kit.onEnable = action
    }

    var cooldown: Double? = null
        get() = kit.cooldown
        set(value) {
            kit.cooldown = value
            field = value
        }
}