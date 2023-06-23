package de.royzer.fabrichg.game

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.data.hgplayer.HGPlayerStatus
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.game.phase.phases.IngamePhase
import net.silkmc.silk.core.text.literalText
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
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
    fun announcePlayerDeath(serverPlayer: ServerPlayer, source: DamageSource) {
        val killer = source.entity
        val killerPlayer = if (killer !is ServerPlayer) null else killer
        val hgPlayer = serverPlayer.hgPlayer
        val otherHGPlayer = killerPlayer?.hgPlayer
        broadcastComponent(
            literalText {
                if (killerPlayer != null) {
                    text("${serverPlayer.name.string}(${hgPlayer.kits.joinToString { it.name }}) wurde von ${killerPlayer.name?.string?.uppercase()}" +
                            "(${otherHGPlayer?.kits?.joinToString { it.name }}) mit ${killerPlayer.mainHandItem?.item} getötet")
                } else {
                    text("${serverPlayer.name.string} ist gestorben")
                }
                color = 0xFFE128
            }
        )
        announceRemainingPlayers()
    }

    fun announceRemainingPlayers() {
        broadcastComponent(
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
