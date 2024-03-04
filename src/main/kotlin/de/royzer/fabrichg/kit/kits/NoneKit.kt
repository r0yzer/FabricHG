package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.kit
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

val noneKit = kit("None") {
    kitSelectorItem = ItemStack(Items.BARRIER)
    description = "no kit"
}