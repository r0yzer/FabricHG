package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.Kit
import de.royzer.fabrichg.kit.events.kititem.isKitItemOf
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.randomKit
import de.royzer.fabrichg.util.everything
import net.minecraft.world.item.Items
import net.silkmc.silk.core.text.literalText
import net.silkmc.silk.core.text.sendText

val COPYCAT_KIT_KEY = "copycatKit"

val copycatKit = kit("Copycat") {
    kitSelectorItem = Items.OCELOT_SPAWN_EGG.defaultInstance

    info { hgPlayer, _ ->
        val kit = hgPlayer.getPlayerData<Kit>(COPYCAT_KIT_KEY) ?: return@info null
        return@info literalText {
            text("Copycat kit: ") { color = TEXT_GRAY }
            text(kit.name) { color = TEXT_BLUE }
        }
    }

    kitEvents {
        onKillPlayer { hgPlayer, _, killed ->
            val serverPlayer = hgPlayer.serverPlayer ?: return@onKillPlayer
            val killedHGPlayer = killed.hgPlayer

            val oldKit = hgPlayer.getPlayerData<Kit>(COPYCAT_KIT_KEY)
            if (oldKit != null) {
                oldKit.onDisable?.invoke(hgPlayer, oldKit)
                val oldKitItems = serverPlayer.inventory.everything.filter {
                    it.isKitItemOf(oldKit)
                }
                oldKitItems.forEach { kitItem ->
                    serverPlayer.inventory.removeItem(kitItem)
                }
            }

            var newKit = killedHGPlayer.kits.randomOrNull() ?: noneKit
            if (copycatExemptKits.contains(newKit)) {
                val exemptKits = listOf(*copycatExemptKits.toTypedArray(), *hgPlayer.kits.toTypedArray())
                newKit = randomKit(exemptKits)
            }

            serverPlayer.sendText {
                text("You are now playing as ") {
                    color = TEXT_GRAY
                }
                text(newKit.name) {
                    color = TEXT_BLUE
                    bold = true
                }
            }

            hgPlayer.playerData[COPYCAT_KIT_KEY] = newKit
            hgPlayer.giveKitItems(newKit)
        }
    }
}

// kits die man nicht bekommen soll
// noch einmal Type checking has run into a recursive problem. Easiest workaround: specify the types of your declarations explicitly. und es passiert was
private val copycatExemptKits: List<Kit> = listOf(
    banditKit, backupKit, noneKit, surpriseKit, copycatKit
)