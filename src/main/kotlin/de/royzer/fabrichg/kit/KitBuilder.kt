package de.royzer.fabrichg.kit

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.kit.events.kit.KitEventsBuilder
import de.royzer.fabrichg.kit.events.kititem.KitItem
import de.royzer.fabrichg.kit.events.kititem.KitItemBuilder
import net.minecraft.server.level.ServerPlayer
import net.silkmc.silk.core.item.setLore
import net.silkmc.silk.core.text.literalText
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class KitBuilder(val kit: Kit) {
    var kitSelectorItem: ItemStack = ItemStack(Items.AIR)
        get() = kit.kitSelectorItem ?: ItemStack(Items.AIR)
        set(value) {
            kit.kitSelectorItem = value
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

    /**
     * Sets the kit description
     * defaults to empty string
     */
    var description: String = ""
        get() = kit.description
        set(value) {
            kit.description = value
            field = value
        }

    private fun addKitItem(kitItem: KitItem) {
        kitItem.itemStack.setLore(listOf(literalText("Kititem")))
        kit.kitItems.add(kitItem)
    }

    fun kitItem(itemStack: ItemStack = Items.BARRIER.defaultInstance, builder: KitItemBuilder.() -> Unit) {
        val kitItem = KitItem(itemStack)
        kitItem.apply { KitItemBuilder(kitItem).apply(builder) }
        addKitItem(kitItem)
    }

    fun onDisable(action: (hgPlayer: HGPlayer, kit: Kit) -> Unit) {
        kit.onDisable = action
    }
    fun onEnable(action: (hgPlayer: HGPlayer, kit: Kit, serverPlayer: ServerPlayer) -> Unit) {
        kit.onEnable = action
    }

    fun kitEvents(builder: KitEventsBuilder.() -> Unit) {
        kit.events.apply { KitEventsBuilder(kit).apply(builder) }
    }

    var cooldown: Double? = null
        get() = kit.cooldown
        set(value) {
            kit.cooldown = value
            field = value
        }
}