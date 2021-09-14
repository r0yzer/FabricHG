package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.Kit
import net.minecraft.item.ItemStack
import net.minecraft.item.Items

object NoneKit : Kit() {
    override val name = "None"
    override val kitSelectorItem = Items.BARRIER
    override val kitItem: ItemStack? = null
}