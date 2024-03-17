package de.royzer.fabrichg.game.phase.phases

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.broadcastComponent
import de.royzer.fabrichg.game.phase.GamePhase
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.scoreboard.formattedTime
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.item.setCustomName
import net.silkmc.silk.core.text.literalText
import net.minecraft.world.item.Items
import net.minecraft.world.level.GameType

object InvincibilityPhase : GamePhase() {
    override fun init() {
        GamePhaseManager.resetTimer()
        broadcastComponent(literalText("HG startet") { color = TEXT_BLUE })
        PlayerList.alivePlayers.forEach { hgPlayer ->
            hgPlayer.serverPlayer?.setGameMode(GameType.SURVIVAL)
            hgPlayer.serverPlayer?.closeContainer()
            hgPlayer.serverPlayer?.removeAllEffects()
            with(hgPlayer.serverPlayer?.inventory) {
                this?.clearContent()
//                this?.add(itemStack(Items.STONE_SWORD, 1) {})
//                repeat(33) { this?.add(itemStack(Items.MUSHROOM_STEW, 1) {}) }
                this?.add(itemStack(Items.COMPASS) {
                    setCustomName { text("Tracker") }
                })
                hgPlayer.giveKitItems()

            }
        }
    }

    override fun tick(timer: Int) {
        when (val timeLeft = maxPhaseTime - timer) {
            180, 120, 60, 30, 15, 10, 5, 4, 3, 2, 1 -> broadcastComponent(literalText("Die Invincibility endet in ") {
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