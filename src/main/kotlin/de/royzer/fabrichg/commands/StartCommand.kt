package de.royzer.fabrichg.commands

import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.phase.phases.LobbyPhase
import net.axay.fabrik.commands.argument
import net.axay.fabrik.commands.command
import net.axay.fabrik.commands.runs
import net.axay.fabrik.commands.simpleExecutes

val startCommand = command("start", true) {
    runs {
        val currentPhase = GamePhaseManager.currentPhase
        if (currentPhase != LobbyPhase) return@runs
        if (GamePhaseManager.timer.get() > currentPhase.maxPhaseTime - 5)
            GamePhaseManager.timer.set(currentPhase.maxPhaseTime - 5)
    }
    argument<Int>("time") time@{
        runs {
            val currentPhase = GamePhaseManager.currentPhase
            if (currentPhase != LobbyPhase) return@runs
            GamePhaseManager.timer.set(currentPhase.maxPhaseTime - this@time.resolveArgument())
        }
    }
}