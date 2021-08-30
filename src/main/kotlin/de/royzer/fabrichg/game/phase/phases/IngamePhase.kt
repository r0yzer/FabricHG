package de.royzer.fabrichg.game.phase.phases

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
    lateinit var winnerInformation: WinnerInformation
    override val phaseType = PhaseType.INGAME
    override val maxPhaseTime = 15 * 60
    override val nextPhase by lazy { EndPhase(winnerInformation) }

    override fun init() {
        broadcast(literalText("${phaseType.name} starting"))
        GamePhaseManager.server.isPvpEnabled = true
        GamePhaseManager.resetTimer()
    }

    override fun tick(timer: Int) {
        if (PlayerList.players.size <= 1) {
            val winnerUUID = PlayerList.players.first()
            winnerInformation = if (GamePhaseManager.server.playerManager.getPlayer(winnerUUID) == null)
                WinnerInformation(
                    combatloggedPlayers[winnerUUID]?.name.orEmpty(),
                    winnerUUID,
                    combatloggedPlayers[winnerUUID]!!.hgPlayerData
                )
            else WinnerInformation(
                GamePhaseManager.server.playerManager.getPlayer(winnerUUID) ?: return
            )
            startNextPhase()
        }
    }
}