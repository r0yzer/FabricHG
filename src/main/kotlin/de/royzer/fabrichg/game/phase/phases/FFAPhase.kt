package de.royzer.fabrichg.game.phase.phases

import de.royzer.fabrichg.bots.player.FakeServerPlayer
import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.kit.kits.backupKit
import de.royzer.fabrichg.kit.kits.noneKit
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.Items
import net.silkmc.silk.core.logging.logInfo

object FFAPhase : IngamePhase() {
    override val phaseType: PhaseType = PhaseType.FFA

    override fun init() {
        GamePhaseManager.server.motd = "${GamePhaseManager.MOTD_STRING}\nCURRENT GAME PHASE: \u00A7eFFA"

        logInfo("FFA startet")

        GamePhaseManager.server.isPvpAllowed = true

        PlayerList.alivePlayers.forEach { hgPlayer ->
            hgPlayer.serverPlayer?.closeContainer()
            if (hgPlayer.serverPlayer is FakeServerPlayer) (hgPlayer.serverPlayer as FakeServerPlayer).hgBot.setItemSlot(
                EquipmentSlot.MAINHAND,
                Items.STONE_SWORD.defaultInstance
            )
        }

        minifeastStartTimes = listOf()
    }

    override fun tick(timer: Int) {
    }

    override fun allowsKitChanges(player: HGPlayer, index: Int): Boolean {
        val isBackup = player.canUseKit(backupKit)
        val isNone = player.kits[index] == noneKit

        return isBackup || isNone
    }
}