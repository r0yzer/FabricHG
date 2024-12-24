package de.royzer.fabrichg.commands

import de.royzer.fabrichg.gui.gameSettins.gameSettingsGUI
import de.royzer.fabrichg.settings.ConfigManager
import kotlinx.coroutines.runBlocking
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.text.sendText
import net.silkmc.silk.igui.openGui

val gameSettingsCommand = command("gamesettings") {
    runs {
        val player = source.player ?: return@runs
        runBlocking {
            if (source.player?.hasPermissions(4) == true || source.player?.name?.string == "r0yzer") {
                player.openGui(gameSettingsGUI(player), 1)
            }
        }
    }
    literal("list") runs {
        source.player?.sendText(ConfigManager.gameSettings.toString())
    }
}