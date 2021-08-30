package de.royzer.fabrichg.game.phase

import de.royzer.fabrichg.game.GamePhaseManager

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
}