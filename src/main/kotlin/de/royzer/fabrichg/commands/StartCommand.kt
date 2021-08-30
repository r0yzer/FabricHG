package de.royzer.fabrichg.commands

import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.phase.phases.LobbyPhase
import net.axay.fabrik.commands.argument
import net.axay.fabrik.commands.command
import net.axay.fabrik.commands.simpleExecutes

val startCommand = command("start", true) {
    simpleExecutes {
        val currentPhase = GamePhaseManager.currentPhase
        if (currentPhase != LobbyPhase) return@simpleExecutes
        GamePhaseManager.timer.set(currentPhase.maxPhaseTime - 5)
    }
    argument<Int>("time") time@{
        simpleExecutes {
            val currentPhase = GamePhaseManager.currentPhase
            if (currentPhase != LobbyPhase) return@simpleExecutes
            GamePhaseManager.timer.set(currentPhase.maxPhaseTime - this@time.resolveArgument())
        }
    }
}