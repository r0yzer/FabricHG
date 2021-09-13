package de.royzer.fabrichg.commands

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.kits
import de.royzer.fabrichg.kit.kits.AnchorKit
import de.royzer.fabrichg.kit.kits.MagmaKit
import net.axay.fabrik.commands.*
import net.axay.fabrik.core.text.literal
import net.axay.fabrik.core.text.sendText
import net.axay.fabrik.igui.GuiType
import net.axay.fabrik.igui.igui
import net.axay.fabrik.igui.openGui

val kitCommand = command("kit") {
    runs {
        this.source.player.openGui(
            igui(GuiType.NINE_BY_FIVE, "Kits".literal, 0) {
                page(0) {}
            }
        )
    }
    argument<String>("kit") {
        suggestList { kits.map { it.name } }
        runs {
            val kitName = resolveArgument()
            val kit = kits.firstOrNull { it.name == kitName }
            if (kit != null)
                source.player.hgPlayer.kits[0] = kit
            else
                source.player.sendText("Es konnte kein Kit mit dem Namen gefunden werden") { color = 0xFF0000 }
        }
    }
    literal("list") {
        runs { source.player.sendText(source.player.hgPlayer.kits.joinToString { it.name }) }
    }
}