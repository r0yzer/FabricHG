package de.royzer.fabrichg.game

import de.royzer.fabrichg.bots.HGBot
import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.data.hgplayer.HGPlayerStatus
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.game.phase.phases.IngamePhase
import net.silkmc.silk.core.text.literalText
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
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


    fun announcePlayerDeath(player: HGPlayer, source: DamageSource, killer: Entity?) {
        val sourceKiller = source.entity
        val otherHGPlayer = killer?.hgPlayer
        broadcastComponent(
            literalText {
                if (killer == sourceKiller && killer != null) {
                    var killMessage = "getötet"
                    if (killer is ServerPlayer) {
                        killMessage = "mit ${killer.mainHandItem?.item.toString().uppercase()} getötet"
                    }
                    if (killer is HGBot) {
                        killMessage = "mit ${killer.mainHandItem?.item.toString().uppercase()} getötet"
                    }
                    text("${player.name}(${player.kits.joinToString { it.name }}) wurde von ${killer.name?.string}" +
                            "(${otherHGPlayer?.kits?.joinToString { it.name }}) $killMessage")
                } else if (killer != null){
                    text("${player.name} wurde von ${killer.name.string} getötet")
                } else {
                    when (val cause = source.type().msgId) {
                        "cactus" -> text("${player.name} ist an einem Kaktus gestorben")
                        "mob_attack" -> text("${player.name} ist an einem Mob gestorben")
                        "fireball" -> text("${player.name} ist an einem Feuerball gestorben")
                        else -> text("${player.name} ist an ${cause.uppercase()} gestorben")
                    }
                }
                color = 0xFFFF55
            }
        )
        announceRemainingPlayers()
    }


    fun announceRemainingPlayers() {
        broadcastComponent(
            literalText {
                val v = if (alivePlayers.size == 1) "verbleibt" else "verbleiben"
                text("Es $v ${alivePlayers.size} Spieler")
                color = 0xFFFF55
            }
        )
    }
}

fun ServerPlayer.removeHGPlayer() {
    hgPlayer.status = HGPlayerStatus.SPECTATOR
    setGameMode(GameType.SPECTATOR)
}

fun HGBot.removeHGPlayer() {
    hgPlayer.status = HGPlayerStatus.SPECTATOR
    kill()
}

