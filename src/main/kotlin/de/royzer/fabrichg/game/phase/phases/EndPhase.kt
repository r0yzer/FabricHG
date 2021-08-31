package de.royzer.fabrichg.game.phase.phases

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.broadcast
import de.royzer.fabrichg.game.combatlog.OfflinePlayer
import de.royzer.fabrichg.game.phase.GamePhase
import de.royzer.fabrichg.game.phase.PhaseType
import net.axay.fabrik.core.text.literalText
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.LiteralText
import java.util.*

class EndPhase(private val hgPlayer: HGPlayer?) : GamePhase() {

    val endTime by lazy { GamePhaseManager.timer.get() }
    private val serverPlayerEntity = hgPlayer?.serverPlayerEntity

    override fun init() {
        serverPlayerEntity?.abilities?.allowFlying = true
        serverPlayerEntity?.abilities?.flying = true
        GamePhaseManager.resetTimer()
    }

    override fun tick(timer: Int) {
        broadcast(winnerText(hgPlayer))
        if (timer >= maxPhaseTime) {
            GamePhaseManager.server.shutdown()
        }
    }

    override val phaseType = PhaseType.END
    override val maxPhaseTime = 20
    override val nextPhase = null
}

fun winnerText(winner: HGPlayer?): LiteralText {
    if (winner == null) return literalText("nunja kein winner wohl")
    return literalText {
        color = 0x00A0FF
        text(winner.name) {
            color = 0xFF1FBB
            underline = true
        }
        text(" hat gewonnen!")
        hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, literalText {
            text("Kills: ${winner.kills}\n") {
                color = 0x00FF51
            }
            text("Kit: womble}") {
                color = 0x42FF51
            }
        })
    }
}