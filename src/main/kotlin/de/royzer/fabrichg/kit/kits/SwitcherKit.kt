package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.Kit
import net.axay.fabrik.core.item.itemStack
import net.minecraft.item.ItemStack
import net.minecraft.item.Items

object SwitcherKit : Kit(){
    override val name = "Switcher"
    override val kitSelectorItem = Items.SNOWBALL
    override val kitItem: ItemStack = itemStack(Items.SNOWBALL) {
        count = 16
    }


}