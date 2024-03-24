package de.royzer.fabrichg.commands

import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.gui.gameSettingsGUI
import de.royzer.fabrichg.settings.GameSettings
import kotlinx.coroutines.runBlocking
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.text.sendText
import net.silkmc.silk.igui.openGui

val gameSettingsCommand = command("gamesettings") {
    requiresPermissionLevel(4)
    runs {
        requires {
            GamePhaseManager.currentPhaseType == PhaseType.LOBBY
        }
        val player = source.player ?: return@runs
        runBlocking {
            player.openGui(gameSettingsGUI(player), 1)
        }
    }
    literal("list") runs {
        source.player?.sendText(GameSettings.toString())
    }
}