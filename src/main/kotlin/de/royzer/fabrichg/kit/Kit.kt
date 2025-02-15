package de.royzer.fabrichg.kit

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.achievements.KitAchievement
import de.royzer.fabrichg.kit.cooldown.cooldown
import de.royzer.fabrichg.kit.events.kit.KitEvents
import de.royzer.fabrichg.kit.events.kititem.KitItem
import de.royzer.fabrichg.kit.info.InfoGenerator
import de.royzer.fabrichg.kit.kits.*
import de.royzer.fabrichg.server
import de.royzer.fabrichg.settings.ConfigManager
import de.royzer.fabrichg.settings.KitProperty
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.silkmc.silk.core.server.players
import net.silkmc.silk.core.text.literalText

class Kit(val name: String) {
    val kitItems = mutableListOf<KitItem>()
    var kitSelectorItem: ItemStack? = null
    var cooldown: Double? = null
    var enabled: Boolean = true
        set(value) {
            field = value
            if (value) {
                // wird enabled, nix sollte passieren
            } else {
                if (!server.isReady) return
                server.players.filter { it.hgPlayer.hasKit(this) }.forEach {
                    onDisable?.invoke(it.hgPlayer, this)
                    val index = it.hgPlayer.kits.indexOf(this)
                    it.hgPlayer.setKit(noneKit, index, force = true)
                    it.sendSystemMessage(literalText {
                        text("Das Kit ")
                        text(this@Kit.name) { color = TEXT_BLUE }
                        text(" wurde in dieser Runde disabled")
                        color = TEXT_GRAY
                    })
                }
            }

        }
    var maxUses: Int? = null
    var usableInInvincibility = true
    var onDisable: ((HGPlayer, Kit) -> Unit)? = null
    var onEnable: ((HGPlayer, Kit, ServerPlayer) -> Unit)? = null
    var events = KitEvents()
    var description: String = ""
    var properties: HashMap<String, KitProperty> = hashMapOf()
    var achievements: MutableList<KitAchievement> = mutableListOf()
    var infoGenerator: InfoGenerator? = null
    var beginnerKit = false

    fun getInfo(player: HGPlayer): Component? {
        val text = infoGenerator?.invoke(player, this)
        if (text != null) return text

        val key = name + "uses"
        val uses = player.getPlayerData<Int>(key) ?: 1

        val remainingUses = (maxUses ?: -10) - uses + 1

        if (player.cooldown(this) > 0.0) return null
        if (remainingUses <= 0) return null

        return literalText {
            text("$name remaining uses: ") { color = TEXT_GRAY }
            text(remainingUses.toString()) { color = TEXT_BLUE }
        }
    }

    override fun toString(): String {
        return name
    }
}

val forbiddenKitCombinations: List<List<Kit>> by lazy {
    ConfigManager.gameSettings.forbiddenKitCombinations.map {
        it.map { kitName ->
            kits.find { it.name == kitName } ?: error("No kit found $kitName for forbidden combination")
        }
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
    endermageKit,
    grapplerKit,
    squidKit,
    zickzackKit,
    berserkerKit,
    tankKit,
    demomanKit,
    banditKit,
    minerKit,
    
    copycatKit,
//    goatKit,
    pacifistKit,
    viperKit,
)

/**
 * Random kit ausser Surprise und None, guckt für Surprise ob auch nur enabled
 */
fun randomKit(): Kit = kits.filter { it != surpriseKit && it != noneKit }
    .filter { if (ConfigManager.gameSettings.surpriseOnlyEnabledKits) it.enabled else true }.randomOrNull() ?: noneKit

/**
 * Einfach ein random Kit ausser die angegebenen
 */
fun randomKit(exempt: List<Kit>): Kit = kits.filter { !exempt.contains(it) }.randomOrNull() ?: noneKit


val enabledKits get() = kits.filter { it.enabled }