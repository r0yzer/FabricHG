package de.royzer.fabrichg.commands

import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.game.phase.phases.LobbyPhase
import de.royzer.fabrichg.util.getRandomHighestPos
import net.silkmc.silk.commands.command


val spawnCommand = command("spawn") {
    runs {
        if (GamePhaseManager.currentPhaseType == PhaseType.LOBBY && !LobbyPhase.isStarting) {
            val pos = getRandomHighestPos(450)
            source.player?.teleportTo(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
        }
    }
}