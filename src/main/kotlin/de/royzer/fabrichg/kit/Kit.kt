package de.royzer.fabrichg.kit

import de.royzer.fabrichg.kit.kits.*
import net.axay.fabrik.core.item.itemStack
import net.axay.fabrik.core.logging.logInfo
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items

class Kit(val name: String) {
    val kitItems = mutableListOf<KitItem>()
    var kitSelectorItem: Item? = null
    var cooldown: Double? = null
}

fun kit(name: String, builder: KitBuilder.() -> Unit): Kit {
    val kit = Kit(name)
    return kit.apply { KitBuilder(kit).apply(builder) }
}

val kits = listOfNotNull(
    anchorKit,
    magmaKit,
    noneKit,
    switcherKit,
    neoKit,
    backupKit,
)