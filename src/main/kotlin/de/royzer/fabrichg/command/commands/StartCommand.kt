package de.royzer.fabrichg.command.commands

import de.royzer.fabrichg.command.hgCommand
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.phase.phases.LobbyPhase
import net.silkmc.silk.commands.command

val startCommand = hgCommand("start") {
    requiresPermissionLevel(4)
    runs {
        val currentPhase = GamePhaseManager.currentPhase
        if (currentPhase != LobbyPhase) return@runs
        if (GamePhaseManager.timer.get() < currentPhase.maxPhaseTime - 5)
            GamePhaseManager.timer.set(currentPhase.maxPhaseTime - 5)
    }
    argument<Int>("time") { timeArg ->
        runs {
            val currentPhase = GamePhaseManager.currentPhase
            if (currentPhase != LobbyPhase) return@runs
            GamePhaseManager.timer.set(currentPhase.maxPhaseTime - timeArg())
        }
    }
}