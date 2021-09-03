package de.royzer.fabrichg.game.phase.phases

import de.royzer.fabrichg.game.GamePhaseManager
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
        broadcast("hg geht los ok :)")
        GamePhaseManager.server.playerManager.playerList.forEach {
            it.teleport(0.0, 100.0, 0.0)
            val highestPos = it.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, BlockPos(0, 100, 0))
            it.teleport(highestPos.x.toDouble(), highestPos.y.toDouble(), highestPos.z.toDouble())
            it.changeGameMode(GameMode.SURVIVAL)
            it.inventory.clear()
            with(it.inventory) {
                insertStack(itemStack(Items.COMPASS) {
                    setCustomName {
                        text("Tracker")
                    }
                })
            }
        }
    }

    override fun tick(timer: Int) {
        when (val timeLeft = maxPhaseTime - timer) {
            180, 120, 60, 30, 15, 10, 5, 4, 3, 2, 1 -> broadcast(literalText("Die Invincibility endet in ") {
                color = 0x7A7A7A
                text(timeLeft.formattedTime) { color = 0x00FFFF }
                text(" Minuten")
            })
            0 -> startNextPhase()
        }
    }

    override val phaseType = PhaseType.INVINCIBILITY
    override val maxPhaseTime = 5 * 1
    override val nextPhase = IngamePhase
}