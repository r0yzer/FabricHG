package de.royzer.fabrichg.game.combatlog

import de.royzer.fabrichg.data.hgplayer.HGPlayerStatus
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.fabrichgScope
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.broadcast
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.game.removeHGPlayer
import kotlinx.coroutines.*
import net.axay.fabrik.core.task.mcSyncLaunch
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

val combatloggedPlayers = hashMapOf<UUID, Job>()

const val maxOfflineTime = 60

fun ServerPlayerEntity.startCombatlog() {
    hgPlayer.status = HGPlayerStatus.DISCONNECTED
    val job = fabrichgScope.launch job@{
        try {
            while (isActive) {
                delay(1000)
                mcSyncLaunch {
                    if (GamePhaseManager.currentPhaseType == PhaseType.INGAME) hgPlayer.offlineTime -= 1
                    if (hgPlayer.offlineTime <= 0) {
                        removeHGPlayer()
                        broadcast("${name.string} ist, nunja, combatlogged und somit tot")
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
