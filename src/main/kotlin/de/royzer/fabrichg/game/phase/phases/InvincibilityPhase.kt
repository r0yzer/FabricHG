package de.royzer.fabrichg.game.phase.phases

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.broadcast
import de.royzer.fabrichg.game.phase.GamePhase
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.scoreboard.formattedTime
import net.axay.fabrik.core.item.itemStack
import net.axay.fabrik.core.item.setCustomName
import net.axay.fabrik.core.text.literalText
import net.minecraft.item.Items
import net.minecraft.util.math.BlockPos
import net.minecraft.world.GameMode
import net.minecraft.world.Heightmap

object InvincibilityPhase : GamePhase() {
    override fun init() {
        GamePhaseManager.resetTimer()
        broadcast(literalText("HG startet :)") { color = TEXT_GRAY })
        broadcast(literalText("Tritt gerne dem offiziellen hg.royzer.de Discord bei\n") {
            color = TEXT_GRAY
            text("https://discord.gg/bS8JKatZkD") { color = TEXT_BLUE }
        })
        PlayerList.alivePlayers.forEach { hgPlayer ->
            hgPlayer.serverPlayerEntity?.changeGameMode(GameMode.SURVIVAL)
            hgPlayer.serverPlayerEntity?.closeHandledScreen()
            with(hgPlayer.serverPlayerEntity?.inventory) {
                this?.clear()
                this?.insertStack(itemStack(Items.COMPASS) {
                    setCustomName { text("Tracker") }
                })
                hgPlayer.kits.forEach { kit ->
                    kit.kitItems.forEach {
                        hgPlayer.serverPlayerEntity?.inventory?.insertStack(it.itemStack.copy())
                    }
                    kit.onEnable?.invoke(hgPlayer, kit)
                }
            }
        }
    }

    override fun tick(timer: Int) {
        when (val timeLeft = maxPhaseTime - timer) {
            180, 120, 60, 30, 15, 10, 5, 4, 3, 2, 1 -> broadcast(literalText("Die Invincibility endet in ") {
                color = TEXT_GRAY
                text(timeLeft.formattedTime) { color = TEXT_BLUE }
                text(" Minuten")
            })
            0 -> startNextPhase()
        }
    }

    override val phaseType = PhaseType.INVINCIBILITY
    override val maxPhaseTime = 120 * 1
    override val nextPhase = IngamePhase
}