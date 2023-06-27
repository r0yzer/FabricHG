package de.royzer.fabrichg.commands

import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.gui.gameOverviewGUI
import net.silkmc.silk.commands.command
import net.silkmc.silk.igui.openGui

val gameCommand = command("game") {
    literal("info") runs {
        requires { GamePhaseManager.currentPhaseType != PhaseType.LOBBY }
        this.source.playerOrException.openGui(gameOverviewGUI(source.player!!), 0)
    }
}