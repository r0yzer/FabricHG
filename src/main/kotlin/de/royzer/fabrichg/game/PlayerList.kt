package de.royzer.fabrichg.game

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.data.hgplayer.HGPlayerStatus
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import net.axay.fabrik.core.text.literalText
import net.minecraft.entity.damage.DamageSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.GameMode
import java.util.*

object PlayerList {
    val players = mutableMapOf<UUID, HGPlayer>()

    val alivePlayers get() = players.filter { it.value.status == HGPlayerStatus.ALIVE || it.value.status == HGPlayerStatus.COMBATLOGGED}

    val spectators get() = players.filter { it.value.status == HGPlayerStatus.SPECTATOR }

    fun getPlayer(uuid: UUID, name: String): HGPlayer {
        return players.computeIfAbsent(uuid) {
            HGPlayer(uuid, name)
        }
    }

    fun getPlayer(uuid: UUID): HGPlayer? {
        return players[uuid]
    }

    fun removePlayer(uuid: UUID) {
        players.remove(uuid)
    }

    fun announcePlayerDeath(name: String, damageSource: DamageSource) {
        broadcast(
            literalText {
                text("$name wurde von ${damageSource.attacker?.name?.asString()} getötet")
                color = 0xFFE128
            }
        )
        announceRemainingPlayers()
    }

    fun announceRemainingPlayers() {
        broadcast(
            literalText {
                text("${alivePlayers.size} players über")
                color = 0xFFE128
            }
        )
    }
}

fun ServerPlayerEntity.removeHGPlayer() {
    PlayerList.removePlayer(uuid)
    hgPlayer.status = HGPlayerStatus.DEAD
    changeGameMode(GameMode.SPECTATOR)
}
