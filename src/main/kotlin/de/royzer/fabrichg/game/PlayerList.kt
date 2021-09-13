package de.royzer.fabrichg.game

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.data.hgplayer.HGPlayerStatus
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.game.phase.phases.IngamePhase
import net.axay.fabrik.core.text.literalText
import net.minecraft.entity.damage.DamageSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.GameMode
import java.util.*

object PlayerList {
    val players = mutableMapOf<UUID, HGPlayer>()

    val alivePlayers get() = players.values.filter { it.status == HGPlayerStatus.ALIVE || it.status == HGPlayerStatus.DISCONNECTED}

    val spectators get() = players.values.filter { it.status == HGPlayerStatus.SPECTATOR }

    val maxPlayers: Int
        get() = if (GamePhaseManager.currentPhaseType == PhaseType.INGAME || GamePhaseManager.currentPhaseType == PhaseType.END) IngamePhase.maxPlayers else alivePlayers.size

    fun addOrGetPlayer(uuid: UUID, name: String): HGPlayer {
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

    // TODO fix
    fun announcePlayerDeath(serverPlayerEntity: ServerPlayerEntity, killer: ServerPlayerEntity?) {
        val hgPlayer = serverPlayerEntity.hgPlayer
        val otherHGPlayer = killer?.hgPlayer
        broadcast(
            literalText {
                text("${serverPlayerEntity.name.string}(${hgPlayer.kits.first().name}) wurde von ${killer?.name?.string}(${otherHGPlayer?.kits?.first()?.name}) getötet")
                color = 0xFFE128
            }
        )
        announceRemainingPlayers()
    }

    fun announceRemainingPlayers() {
        broadcast(
            literalText {
                text("Es verbleiben ${alivePlayers.size} Spieler")
                color = 0xFFE128
            }
        )
    }
}

fun ServerPlayerEntity.removeHGPlayer() {
    PlayerList.removePlayer(uuid)
    hgPlayer.status = HGPlayerStatus.SPECTATOR
    changeGameMode(GameMode.SPECTATOR)
}
