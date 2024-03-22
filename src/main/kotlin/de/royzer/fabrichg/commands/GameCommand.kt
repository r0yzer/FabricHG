package de.royzer.fabrichg.commands

import de.royzer.fabrichg.gui.gameOverviewGUI
import net.silkmc.silk.commands.command
import net.silkmc.silk.igui.openGui

val gameCommand = command("game") {
    runs {
        val player = source.player ?: return@runs
        player.openGui(gameOverviewGUI(player), 1)
    }
}