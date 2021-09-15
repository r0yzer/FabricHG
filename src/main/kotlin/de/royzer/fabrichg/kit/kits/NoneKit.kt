package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.kit
import net.minecraft.item.ItemStack
import net.minecraft.item.Items

val noneKit = kit("None") {
    kitSelectorItem = ItemStack(Items.BARRIER)
}