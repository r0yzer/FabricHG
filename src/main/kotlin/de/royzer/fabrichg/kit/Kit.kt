package de.royzer.fabrichg.kit

import de.royzer.fabrichg.kit.kits.AnchorKit
import de.royzer.fabrichg.kit.kits.MagmaKit
import de.royzer.fabrichg.kit.kits.NoneKit
import de.royzer.fabrichg.kit.kits.SwitcherKit
import net.minecraft.item.Item
import net.minecraft.item.ItemStack


abstract class Kit {
    abstract val name: String
    abstract val kitSelectorItem: Item
    abstract val kitItem: ItemStack?
}

class KKit(
    val name: String,
    val kitSelectorItem: Item,
    val kitItem: ItemStack?
) {

}

fun kit(name: String, kitSelectorItem: Item) = KKit(name, kitSelectorItem, null)

val kits = listOf(
    MagmaKit,
    NoneKit,
    AnchorKit,
    SwitcherKit,
)