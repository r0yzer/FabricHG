package de.royzer.fabrichg.game.phase.phases

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.broadcast
import de.royzer.fabrichg.game.combatlog.combatloggedPlayers
import de.royzer.fabrichg.game.phase.GamePhase
import de.royzer.fabrichg.game.phase.PhaseType
import net.axay.fabrik.core.text.literalText
import net.minecraft.client.RunArgs
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

object IngamePhase : GamePhase() {
    var winner: HGPlayer? = null
    override val phaseType = PhaseType.INGAME
    override val maxPhaseTime = 5 * 60
    override val nextPhase by lazy { EndPhase(winner) }

    val maxPlayers by lazy { PlayerList.alivePlayers.size }

    override fun init() {
        GamePhaseManager.server.isPvpEnabled = true
    }

    override fun tick(timer: Int) {
        if (PlayerList.alivePlayers.size <= 1) {
            winner = PlayerList.alivePlayers.firstOrNull()
            startNextPhase()
        }
        when (val timeLeft = maxPhaseTime - timer) {
            60, 30, 15, 10, 5, 4, 3, 2, 1 -> broadcast(literalText("spiel endet in $timeLeft"))
            0 -> {
                winner = PlayerList.alivePlayers.random()
                startNextPhase()
            }
        }
    }
}