package de.royzer.fabrichg.kit

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.kit.events.kit.KitEventsBuilder
import de.royzer.fabrichg.kit.events.kititem.KitItem
import de.royzer.fabrichg.kit.events.kititem.KitItemBuilder
import de.royzer.fabrichg.kit.info.InfoGenerator
import net.minecraft.core.component.DataComponents
import net.minecraft.server.level.ServerPlayer
import net.silkmc.silk.core.item.setLore
import net.silkmc.silk.core.text.literalText
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.CustomData
import net.silkmc.silk.nbt.dsl.nbtCompound

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

    var maxUses: Int? = null
        get() = kit.maxUses
        set(value) {
            kit.maxUses = value
            field = value
        }

    var alternativeMaxUses: Int? = null
        get() = kit.alternativeMaxUses
        set(value) {
            kit.alternativeMaxUses = value
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

    /**
     * Ob man nur unter ner bestimmten k/d / kills das spielen kann
     */
    var beginnerKit: Boolean = false
        get() = kit.beginnerKit
        set(value) {
            kit.beginnerKit = value
            field = value
        }

    private fun addKitItem(kitItem: KitItem) {
        kitItem.itemStack.setLore(listOf(literalText("Kititem")))
        kitItem.itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbtCompound {
            put("kit", kit.name)
        }))
        kit.kitItems.add(kitItem)
    }

    fun kitItem(itemStack: ItemStack = Items.BARRIER.defaultInstance, builder: KitItemBuilder.() -> Unit): KitItem {
        val kitItem = KitItem(itemStack, kit)
        kitItem.apply { KitItemBuilder(kitItem).apply(builder) }
        addKitItem(kitItem)
        return kitItem
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

    fun info(generator: InfoGenerator) {
        kit.infoGenerator = generator
    }

    var cooldown: Double? = null
        get() = kit.cooldown
        set(value) {
            kit.cooldown = value
            field = value
        }
}