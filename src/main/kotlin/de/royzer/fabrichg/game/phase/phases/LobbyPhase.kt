package de.royzer.fabrichg.game.phase.phases

import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.broadcast
import de.royzer.fabrichg.game.phase.GamePhase
import de.royzer.fabrichg.game.phase.PhaseType
import net.axay.fabrik.core.text.literalText

object LobbyPhase : GamePhase() {
    override val phaseType = PhaseType.LOBBY
    override val maxPhaseTime = 60
    override val nextPhase = InvincibilityPhase

    override fun init() {
        GamePhaseManager.server.isPvpEnabled = false
    }

    override fun tick(timer: Int) {
        val timeLeft = maxPhaseTime - timer

        if (PlayerList.players.size >= 2) {
            when (timeLeft) {
                180, 120, 60, 30, 15, 10, 5, 4, 3, 2, 1 -> broadcast(literalText("spiel start in $timeLeft"))
                0 -> startNextPhase()
            }
        } else GamePhaseManager.resetTimer()
    }
}