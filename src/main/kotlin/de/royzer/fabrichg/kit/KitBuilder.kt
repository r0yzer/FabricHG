package de.royzer.fabrichg.kit

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.kit.events.KitEventsBuilder
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

    /**
     * Set if the kit should be useable in the invincibility time
     * defaults to true
     */
    var usableInInvincibility: Boolean = true
        get() = kit.usableInInvincibility
        set(value) {
            kit.usableInInvincibility = value
            field = value
        }

    private fun addKitItem(kitItem: KitItem) {
        kitItem.itemStack.setLore(listOf(literalText("Kititem")))
        kit.kitItems.add(kitItem)
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

    fun events(builder: KitEventsBuilder.() -> Unit) {
        kit.events.apply { KitEventsBuilder(kit).apply(builder) }
    }

    var cooldown: Double? = null
        get() = kit.cooldown
        set(value) {
            kit.cooldown = value
            field = value
        }
}