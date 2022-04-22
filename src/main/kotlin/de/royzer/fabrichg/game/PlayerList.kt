package de.royzer.fabrichg.game

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.data.hgplayer.HGPlayerStatus
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.game.phase.phases.IngamePhase
import net.axay.fabrik.core.text.literalText
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.GameType
import java.util.*

object PlayerList {
    val players = mutableMapOf<UUID, HGPlayer>()

    val alivePlayers get() = players.values.filter { it.status == HGPlayerStatus.ALIVE || it.status == HGPlayerStatus.DISCONNECTED }

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
    fun announcePlayerDeath(serverPlayerEntity: ServerPlayer, killer: ServerPlayer?) {
        val hgPlayer = serverPlayerEntity.hgPlayer
        val otherHGPlayer = killer?.hgPlayer
        broadcast(
            literalText {
                text("${serverPlayerEntity.name.string}(${hgPlayer.kits.joinToString { it.name }}) wurde von ${killer?.name?.string}(${otherHGPlayer?.kits?.joinToString { it.name }}) mit ${killer?.mainHandItem?.item} getötet")
                color = 0xFFE128
            }
        )
        announceRemainingPlayers()
    }

    fun announceRemainingPlayers() {
        broadcast(
            literalText {
                text("Es verbleiben ${alivePlayers.size - 1} Spieler")
                color = 0xFFE128
            }
        )
    }
}

fun ServerPlayer.removeHGPlayer() {
    hgPlayer.status = HGPlayerStatus.SPECTATOR
    setGameMode(GameType.SPECTATOR)
}
