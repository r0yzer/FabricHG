package de.royzer.fabrichg.game.phase

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.game.GamePhaseManager
import net.silkmc.silk.commands.PermissionLevel

abstract class GamePhase {
    abstract fun init()
    abstract fun tick(timer: Int)
    abstract val phaseType: PhaseType
    abstract val maxPhaseTime: Int
    abstract val nextPhase: GamePhase?

    fun startNextPhase() {
        nextPhase?.init()
        GamePhaseManager.currentPhase = nextPhase ?: return
    }

    abstract fun allowsKitChanges(player: HGPlayer, index: Int): Boolean

    open fun allowsForKitChangesForPlayer(player: HGPlayer, index: Int): Boolean {
        val serverPlayer = player.serverPlayer ?: return false

        if (serverPlayer.hasPermissions(PermissionLevel.OWNER.level)) return true

        return allowsKitChanges(player, index)
    }
}