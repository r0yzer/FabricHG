package de.royzer.fabrichg.commands

import de.royzer.fabrichg.gui.kitAchievementsGui
import net.silkmc.silk.commands.command
import net.silkmc.silk.igui.openGui

val achievementsCommand = command("achievements") {
    runs {
        val player = source.player ?: return@runs

        player.openGui(kitAchievementsGui(player), 1)
    }
}