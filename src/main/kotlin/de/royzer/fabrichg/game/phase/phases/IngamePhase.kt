package de.royzer.fabrichg.game.phase.phases

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.feast.Feast
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
    override val maxPhaseTime = 15 * 60
    override val nextPhase by lazy { EndPhase(winner) }

    val feastStartTime = 600

    val maxPlayers by lazy { PlayerList.alivePlayers.size }

    override fun init() {
        GamePhaseManager.server.isPvpEnabled = true
        PlayerList.alivePlayers.forEach { hgPlayer ->
            hgPlayer.serverPlayerEntity?.closeHandledScreen()
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
            60, 30, 15, 10, 5, 4, 3, 2, 1 -> broadcast(literalText {
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