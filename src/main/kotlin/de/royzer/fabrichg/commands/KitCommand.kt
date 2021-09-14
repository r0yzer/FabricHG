package de.royzer.fabrichg.commands

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.kits
import de.royzer.fabrichg.kit.kits.AnchorKit
import de.royzer.fabrichg.kit.kits.MagmaKit
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
        val hgPlayer = this.source.player.hgPlayer
        this.source.player.openGui(
            igui(GuiType.NINE_BY_SIX, "Kits".literal, 1) {
                page(1) {
                    placeholder(Slots.Border, Items.WHITE_STAINED_GLASS_PANE.guiIcon)

                    val compound = compound(
                        (2 sl 2) rectTo (5 sl 8),
                        kits.toGuiList(),
                        iconGenerator = { kit ->
                            itemStack(kit.kitSelectorItem) {
                                setCustomName {
                                    text(kit.name) {
                                        color = if (hgPlayer.hasKit(kit)) 0x00FF00 else 0x00FFFF
                                        strikethrough = false
                                        bold = false
                                        italic = false
                                    }
                                }
                            }
                        },
                        onClick = { _, kit ->
                            this@runs.source.player.hgPlayer.kits[0] = kit
                            this@runs.source.player.closeHandledScreen()
                        }
                    )

                    compoundScrollForwards(1 sl 9, Items.RED_CONCRETE.guiIcon, compound)
                    compoundScrollBackwards(6 sl 9, Items.GREEN_CONCRETE.guiIcon, compound)
                }
            }, 1
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
        runs { source.player.sendText(source.player.hgPlayer.kits.joinToString { it.name }) }
    }
}