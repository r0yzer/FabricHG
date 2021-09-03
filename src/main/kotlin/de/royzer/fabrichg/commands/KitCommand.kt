package de.royzer.fabrichg.commands

import net.axay.fabrik.commands.command
import net.axay.fabrik.commands.runs
import net.axay.fabrik.core.text.literal
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
}