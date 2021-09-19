package de.royzer.fabrichg.commands

import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.phase.phases.LobbyPhase
import net.axay.fabrik.commands.command

val startCommand = command("start", true) {
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