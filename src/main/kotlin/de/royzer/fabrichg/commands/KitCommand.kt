package de.royzer.fabrichg.commands

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.broadcast
import de.royzer.fabrichg.gui.kitSelectorGUI
import de.royzer.fabrichg.kit.Kit
import de.royzer.fabrichg.kit.kits
import net.axay.fabrik.commands.*
import net.axay.fabrik.core.item.itemStack
import net.axay.fabrik.core.item.setCustomName
import net.axay.fabrik.core.text.literal
import net.axay.fabrik.core.text.sendText
import net.axay.fabrik.igui.*
import net.axay.fabrik.igui.events.GuiClickEvent
import net.axay.fabrik.igui.observable.toGuiList
import net.minecraft.item.Items

val kitCommand = command("kit") command@{
    runs runs@{
        this.source.player.openGui(
            kitSelectorGUI(this.source.player),1
        )
    }
    argument<String>("kit") {
        suggestList { kits.map { it.name } }
        runs {
            val kitName = resolveArgument()
            val kit = kits.firstOrNull { it.name.equals(kitName, true) }
            if (kit != null)
                source.player.hgPlayer.kits[0] = kit
            else
                source.player.sendText("Es konnte kein Kit mit dem Namen gefunden werden") { color = 0xFF0000 }
        }
    }
    literal("list") {
        runs { source.player.sendText(source.player.hgPlayer.kits.joinToString { it.name }) { color = 0x00FF00 } }
    }
}