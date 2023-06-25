package de.royzer.fabrichg.commands

import de.royzer.fabrichg.game.GamePhaseManager
import net.silkmc.silk.commands.command

val phaseCommand = command("phase") {
    literal("skip") runs {
        GamePhaseManager.currentPhase.startNextPhase()
    }
}