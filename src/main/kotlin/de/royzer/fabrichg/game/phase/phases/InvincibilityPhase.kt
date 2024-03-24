package de.royzer.fabrichg.game.phase.phases

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.broadcastComponent
import de.royzer.fabrichg.game.phase.GamePhase
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.kit.kits.surpriseKit
import de.royzer.fabrichg.kit.randomKit
import de.royzer.fabrichg.scoreboard.formattedTime
import de.royzer.fabrichg.util.tracker
import net.minecraft.world.level.GameType
import net.silkmc.silk.core.text.literalText

object InvincibilityPhase : GamePhase() {
    override fun init() {
        GamePhaseManager.server.motd = "${GamePhaseManager.MOTD_STRING}\nCURRENT GAME PHASE: \u00A7aINVINCIBILITY"
        GamePhaseManager.resetTimer()
        broadcastComponent(literalText("HG startet") { color = TEXT_BLUE })
        PlayerList.alivePlayers.forEach { hgPlayer ->
            hgPlayer.serverPlayer?.setGameMode(GameType.SURVIVAL)
            hgPlayer.serverPlayer?.closeContainer()
            hgPlayer.serverPlayer?.removeAllEffects()
            if (hgPlayer.hasKit(surpriseKit)) {
                hgPlayer.kits.remove(surpriseKit)
                hgPlayer.kits.add(randomKit())
            }
            with(hgPlayer.serverPlayer?.inventory) {
                this?.clearContent()
//                this?.add(itemStack(Items.STONE_SWORD, 1) {})
//                repeat(33) { this?.add(itemStack(Items.MUSHROOM_STEW, 1) {}) }
                this?.add(tracker)
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