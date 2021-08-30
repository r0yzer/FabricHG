package de.royzer.fabrichg.game.phase.phases

import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.broadcast
import de.royzer.fabrichg.game.phase.GamePhase
import de.royzer.fabrichg.game.phase.PhaseType
import net.axay.fabrik.core.text.literalText
import net.minecraft.world.GameMode

object InvincibilityPhase : GamePhase() {
    override fun init() {
        GamePhaseManager.resetTimer()
        broadcast("hg geht los ok")
        GamePhaseManager.server.playerManager.playerList.forEach {
            it.teleport(0.0, 100.0, 0.0)
            it.changeGameMode(GameMode.SURVIVAL)
        }
    }

    override fun tick(timer: Int) {
        when (val timeLeft = maxPhaseTime - timer) {
            180, 120, 60, 30, 15, 10, 5, 4, 3, 2, 1 -> broadcast(literalText("invincibility endet in $timeLeft"))
            0 -> startNextPhase()
        }
    }

    override val phaseType = PhaseType.INVINCIBILITY
    override val maxPhaseTime = 2 * 1
    override val nextPhase = IngamePhase
}