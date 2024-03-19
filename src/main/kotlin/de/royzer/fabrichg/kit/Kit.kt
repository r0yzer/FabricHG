package de.royzer.fabrichg.kit

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.kit.events.kit.KitEvents
import de.royzer.fabrichg.kit.kits.*
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

class Kit(val name: String) {
    val kitItems = mutableListOf<KitItem>()
    var kitSelectorItem: ItemStack? = null
    var cooldown: Double? = null
    var usableInInvincibility = true
    var onDisable: ((HGPlayer, Kit) -> Unit)? = null
    var onEnable: ((HGPlayer, Kit, ServerPlayer) -> Unit)? = null
    var events = KitEvents()
    var description: String = ""

    override fun toString(): String {
        return name
    }
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
    scoutKit,
    reviveKit,
    kangarooKit,
    blinkKit,
    diggerKit,
    cannibalKit,
//    hulkKit,
    mirrorKit,
    monkKit,
    urgalKit,
    gravityKit,
    snailKit,
    phantomKit,
    thorKit,
    beerKit,
    beeKit,
    jackhammerKit,
    lumberjackKit,
    surpriseKit,
    perfectKit,
    gamblerKit
)

fun randomKit() = kits.filter { it != surpriseKit && it != noneKit }.random()