package de.royzer.fabrichg.game.phase.phases

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.broadcastComponent
import de.royzer.fabrichg.game.phase.GamePhase
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.scoreboard.formattedTime
import de.royzer.fabrichg.util.getRandomHighestPos
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.silkmc.silk.core.text.literalText
import net.silkmc.silk.core.kotlin.ticks
import net.silkmc.silk.core.task.mcCoroutineScope

object LobbyPhase : GamePhase() {
    override val phaseType = PhaseType.LOBBY
    override val maxPhaseTime = 60 * 3
    override val nextPhase = InvincibilityPhase

    var isStarting = false

    override fun init() {
        GamePhaseManager.server.isPvpAllowed = false
        GamePhaseManager.server.motd = "${GamePhaseManager.MOTD_STRING}\nCURRENT GAME PHASE: \u00A72LOBBY"
    }

    override fun tick(timer: Int) {
        val timeLeft = maxPhaseTime - timer

        if (PlayerList.players.size >= 2) {
            when (timeLeft) {
                15 -> {
                    isStarting = true
                    PlayerList.alivePlayers.forEach {
                        val pos = getRandomHighestPos(20)
                        it.serverPlayer?.teleportTo(
                            pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble()
                        )
                        mcCoroutineScope.launch {
                            while (isStarting) {
                                it.serverPlayer?.teleportTo(
                                    pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble()
                                )
                                delay(1.ticks)
                            }
                        }
                    }
                    broadcastComponent(literalText("Das Spiel start in ") {
                        color = TEXT_GRAY
                        text(timeLeft.formattedTime) { color = TEXT_BLUE }
                        text(" Minuten")
                    })
                }
                180, 120, 60, 30, 10, 5, 4, 3, 2, 1 -> broadcastComponent(literalText("Das Spiel start in ") {
                    color = TEXT_GRAY
                    text(timeLeft.formattedTime) { color = TEXT_BLUE }
                    text(" Minuten")
                })
                0 -> {
                    isStarting = false
                    startNextPhase()
                }
            }
            if (timeLeft > 15 && isStarting) {
                isStarting = false
            }
        } else {
            isStarting = false
            GamePhaseManager.resetTimer()
            PlayerList.alivePlayers.forEach { hgPlayer ->
                hgPlayer.serverPlayer?.removeAllEffects()
            }
        }
    }

    override fun allowsKitChanges(player: HGPlayer, index: Int): Boolean {
        return true
    }
}