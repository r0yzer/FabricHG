package de.royzer.fabrichg.game.combatlog

import de.royzer.fabrichg.data.hgplayer.HGPlayerStatus
import de.royzer.fabrichg.data.hgplayer.hgPlayerData
import de.royzer.fabrichg.fabrichgScope
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.broadcast
import kotlinx.coroutines.*
import net.axay.fabrik.core.task.mcSyncLaunch
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*


val combatloggedPlayers = hashMapOf<UUID, OfflinePlayer>()

fun ServerPlayerEntity.startCombatlog() {
    hgPlayerData.status = HGPlayerStatus.COMBATLOGGED
    val job = fabrichgScope.launch {
        while (isActive) {
            mcSyncLaunch {
                hgPlayerData.combatlogTime -= 1
                if (hgPlayerData.combatlogTime <= 0) {
                    PlayerList.removePlayer(uuid)
                    hgPlayerData.status = HGPlayerStatus.DEAD
                    broadcast("$name ist, nunja, combatlogged und somit tot")
                    this.cancel()
                }
            }.join()
            delay(1000)
        }
    }
    combatloggedPlayers[uuid] = OfflinePlayer(
        name.string,
        uuid,
        hgPlayerData,
        job
    )
}
