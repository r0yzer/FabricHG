package de.royzer.fabrichg.commands

import de.royzer.fabrichg.gui.gameSettins.gameSettingsGUI
import de.royzer.fabrichg.settings.ConfigManager
import de.royzer.fabrichg.util.isOP
import de.royzer.fabrichg.util.openSpectatorClickableGUI
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.kotlin.ticks
import net.silkmc.silk.core.task.infiniteMcCoroutineTask
import net.silkmc.silk.core.task.mcCoroutineTask
import net.silkmc.silk.core.text.sendText
import net.silkmc.silk.igui.openGui
import kotlin.time.Duration.Companion.seconds

val gameSettingsCommand = command("gamesettings") {
    runs {
        val player = source.player ?: return@runs
        runBlocking {
            if (source.player?.isOP() == true) {
                player.openSpectatorClickableGUI(gameSettingsGUI(player), 1)
            }
        }
    }
    literal("list") runs {
        source.player?.sendText(ConfigManager.gameSettings.toString())
    }
}