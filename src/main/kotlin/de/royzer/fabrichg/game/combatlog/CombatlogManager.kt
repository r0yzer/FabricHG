package de.royzer.fabrichg.game.combatlog

import de.royzer.fabrichg.data.hgplayer.HGPlayerStatus
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.fabrichgScope
import de.royzer.fabrichg.game.*
import de.royzer.fabrichg.game.phase.PhaseType
import kotlinx.coroutines.*
import net.silkmc.silk.core.task.mcSyncLaunch
import net.silkmc.silk.core.text.literalText
import net.minecraft.server.level.ServerPlayer
import java.util.*

val combatloggedPlayers = hashMapOf<UUID, Job>()

const val maxOfflineTime = 60

fun ServerPlayer.startCombatlog() {
    hgPlayer.status = HGPlayerStatus.DISCONNECTED
    val job = fabrichgScope.launch job@{
        try {
            while (isActive) {
                delay(1000)
                mcSyncLaunch {
                    if (GamePhaseManager.currentPhaseType == PhaseType.INGAME) hgPlayer.offlineTime -= 1
                    if (hgPlayer.offlineTime <= 0) {
                        removeHGPlayer()
                        broadcastComponent(literalText("${name.string} war zu lange offline") { color = 0xFFE128 })
                        PlayerList.announceRemainingPlayers()
                        this@job.cancel()
                    }
                }.join()
            }
        } finally {
            if (hgPlayer.status == HGPlayerStatus.ALIVE) {
                combatloggedPlayers.remove(uuid)
            }
        }
    }
    combatloggedPlayers[uuid] = job
}
