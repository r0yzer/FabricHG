package de.royzer.fabrichg.game.phase.phases

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.broadcast
import de.royzer.fabrichg.game.phase.GamePhase
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.scoreboard.formattedTime
import net.axay.fabrik.core.text.literalText
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.Heightmap
import kotlin.random.Random

object LobbyPhase : GamePhase() {
    override val phaseType = PhaseType.LOBBY
    override val maxPhaseTime = 60 * 3
    override val nextPhase = InvincibilityPhase

    var isStarting = false

    override fun init() {
        GamePhaseManager.server.isPvpEnabled = false
    }

    override fun tick(timer: Int) {
        val timeLeft = maxPhaseTime - timer

        if (PlayerList.players.size >= 2) {
            when (timeLeft) {
                15 -> {
                    isStarting = true
                    PlayerList.alivePlayers.forEach {
                        val x = Random.nextInt(-20, 20)
                        val z = Random.nextInt(-20, 20)
                        it.serverPlayerEntity?.teleport(
                            x.toDouble(),
                            it.serverPlayerEntity!!.world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z).toDouble(),
                            z.toDouble()
                        )
                        it.serverPlayerEntity?.freeze()
                    }
                }
                180, 120, 60, 30, 15, 10, 5, 4, 3, 2, 1 -> broadcast(literalText("Das Spiel start in ") {
                    color = TEXT_GRAY
                    text(timeLeft.formattedTime) { color = TEXT_BLUE }
                    text(" Minuten")
                })
                0 -> startNextPhase()
            }
            if (timeLeft >= 15 && isStarting)
                isStarting = false
        } else {
            isStarting = false
            GamePhaseManager.resetTimer()
            PlayerList.alivePlayers.forEach { hgPlayer ->
                hgPlayer.serverPlayerEntity?.clearStatusEffects()
            }
        }
    }
}

fun ServerPlayerEntity.freeze() {
    this.addStatusEffect(StatusEffectInstance(StatusEffects.SLOWNESS, 15 * 20, 255, false, false))
    this.addStatusEffect(StatusEffectInstance(StatusEffects.JUMP_BOOST, 15 * 20, 129, false, false))
}