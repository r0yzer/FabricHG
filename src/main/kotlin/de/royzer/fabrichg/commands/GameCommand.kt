package de.royzer.fabrichg.commands

import de.royzer.fabrichg.gui.gameOverviewGUI
import net.silkmc.silk.commands.command
import net.silkmc.silk.igui.openGui

val gameCommand = command("game") {
    literal("info") runs {
        this.source.playerOrException.openGui(gameOverviewGUI)
    }
}