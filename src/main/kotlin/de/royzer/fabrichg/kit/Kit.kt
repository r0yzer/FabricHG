package de.royzer.fabrichg.kit

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.kit.kits.*
import net.minecraft.item.Item

class Kit(val name: String) {
    val kitItems = mutableListOf<KitItem>()
    var kitSelectorItem: Item? = null
    var cooldown: Double? = null
    var onDisable: ((HGPlayer, Kit) -> Unit)? = null
    var onEnable: ((HGPlayer, Kit) -> Unit)? = null
}

inline fun kit(name: String, builder: KitBuilder.() -> Unit): Kit {
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
    rougeKit,
)