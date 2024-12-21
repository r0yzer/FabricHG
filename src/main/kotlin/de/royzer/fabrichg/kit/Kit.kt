package de.royzer.fabrichg.kit

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.events.kit.KitEvents
import de.royzer.fabrichg.kit.events.kititem.KitItem
import de.royzer.fabrichg.kit.kits.*
import de.royzer.fabrichg.server
import de.royzer.fabrichg.settings.KitProperty
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.silkmc.silk.core.server.players

class Kit(val name: String) {
    val kitItems = mutableListOf<KitItem>()
    var kitSelectorItem: ItemStack? = null
    var cooldown: Double? = null
    var enabled: Boolean = true
        set(value) {
            field = value
            server.players.filter { it.hgPlayer.hasKit(this) }.forEach {
                onDisable?.invoke(it.hgPlayer, this)
                val index = it.hgPlayer.kits.indexOf(this)
                it.hgPlayer.setKit(this, index)
            }
        }
    var maxUses: Int? = null
    var usableInInvincibility = true
    var onDisable: ((HGPlayer, Kit) -> Unit)? = null
    var onEnable: ((HGPlayer, Kit, ServerPlayer) -> Unit)? = null
    var events = KitEvents()
    var description: String = ""
    var properties: HashMap<String, KitProperty> = hashMapOf()

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
    hulkKit,
    mirrorKit,
    monkKit,
    urgalKit,
    gravityKit,
    snailKit,
    phantomKit,
    thorKit,
    beerKit, // wenn buster das fixt
    beeKit,
    jackhammerKit,
    lumberjackKit,
    surpriseKit,
    perfectKit,
    gamblerKit,
    evokerKit,
    spitKit,
    ninjaKit,
    frozenKit,
    beamKit,
    stalaktitKit,
    eberKit,
    reaperKit,
    jokerKit,
    stomperKit,
    poseidonKit,
//    witchKit,
    turtleKit,
//    frogKit,
    gliderKit,
    gladiatorKit,
    automaticKit,
    trymacsKit,
)

fun randomKit(): Kit = kits.filter { it != surpriseKit && it != noneKit }.random()