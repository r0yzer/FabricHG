package de.royzer.fabrichg.game.phase.phases

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.feast.Feast
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.broadcastComponent
import de.royzer.fabrichg.game.phase.GamePhase
import de.royzer.fabrichg.game.phase.PhaseType
import net.silkmc.silk.core.logging.logInfo
import net.silkmc.silk.core.text.literalText

object IngamePhase : GamePhase() {
    var winner: HGPlayer? = null
    override val phaseType = PhaseType.INGAME
    override val maxPhaseTime = 30 * 60
    override val nextPhase by lazy { EndPhase(winner) }

    private const val feastStartTime = 600

    val maxPlayers by lazy { PlayerList.alivePlayers.size }

    override fun init() {
        logInfo("IngamePhase startet")
        GamePhaseManager.server.isPvpAllowed = true
        PlayerList.alivePlayers.forEach { hgPlayer ->
            hgPlayer.serverPlayer?.closeContainer()
        }
    }

    override fun tick(timer: Int) {
        if (PlayerList.alivePlayers.size <= 1) {
            winner = PlayerList.alivePlayers.firstOrNull()
            startNextPhase()
        }

        if (timer == feastStartTime) {
            Feast.start()
        }

        when (val timeLeft = maxPhaseTime - timer) {
            60, 30, 15, 10, 5, 4, 3, 2, 1 -> broadcastComponent(literalText {
                text("Das Spiel endet in ")
                text(timeLeft.toString()) { color = TEXT_BLUE }
                text(" Sekunden")
                color = TEXT_GRAY
            })
            0 -> {
                winner = PlayerList.alivePlayers.shuffled().maxByOrNull { it.kills }
                startNextPhase()
            }
        }
    }
}