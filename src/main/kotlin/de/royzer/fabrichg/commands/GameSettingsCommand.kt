package de.royzer.fabrichg.commands

import de.royzer.fabrichg.gui.gameSettins.gameSettingsGUI
import de.royzer.fabrichg.settings.ConfigManager
import de.royzer.fabrichg.util.luckperms.hasPermission
import de.royzer.fabrichg.util.openSpectatorClickableGUI
import kotlinx.coroutines.runBlocking
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.text.sendText

val gameSettingsCommand = command("gamesettings") {
    runs {
        val player = source.player ?: return@runs
        runBlocking {
            if (source.player?.hasPermission("gamesettings") == true) {
                player.openSpectatorClickableGUI(gameSettingsGUI(player), 1)
            }
        }
    }
    literal("list") runs {
        source.player?.sendText(ConfigManager.gameSettings.toString())
    }
}
