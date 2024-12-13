package de.royzer.fabrichg.command.commands

import de.royzer.fabrichg.command.hgCommand
import de.royzer.fabrichg.game.GamePhaseManager
import net.silkmc.silk.commands.command

val phaseCommand = hgCommand("phase") {
    requiresPermissionLevel(1)
    literal("skip") runs {
        GamePhaseManager.currentPhase.startNextPhase()
    }
}