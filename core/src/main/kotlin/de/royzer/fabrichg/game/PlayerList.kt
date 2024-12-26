package de.royzer.fabrichg.game

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.data.hgplayer.HGPlayerStatus
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import net.silkmc.silk.core.text.literalText
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.GameType
import net.silkmc.silk.core.text.sendText
import java.util.*

object PlayerList {
    val players = mutableMapOf<UUID, HGPlayer>()

    val aliveOrGulagPlayers get() = players.values.filter { it.status == HGPlayerStatus.ALIVE || it.status == HGPlayerStatus.DISCONNECTED || it.status == HGPlayerStatus.GULAG }
    val alivePlayers get() = players.values.filter { it.status == HGPlayerStatus.ALIVE || it.status == HGPlayerStatus.DISCONNECTED }

    val spectators get() = players.values.filter { it.status == HGPlayerStatus.SPECTATOR }

    val maxPlayers: Int
        get() = fabricHGRuntime.maxPlayers

    fun addOrGetPlayer(uuid: UUID, name: String): HGPlayer {
        return players.getOrPut(uuid) {
            HGPlayer(uuid, name).also { it.status = HGPlayerStatus.SPECTATOR }
        }
    }

    fun getPlayer(uuid: UUID): HGPlayer? {
        return players[uuid]
    }

    fun removePlayer(uuid: UUID) {
        players.remove(uuid)
    }
}

fun ServerPlayer.removeHGPlayer() {
    hgPlayer.kits.forEach {
        it.onDisable?.invoke(hgPlayer, it)
    }
    hgPlayer.status = HGPlayerStatus.SPECTATOR
    setGameMode(GameType.SPECTATOR)
    sendText {
        text("Use the ")
        text("/info ") {
            bold = true
            color = TEXT_BLUE
        }
        text("command to get information about this round")
        italic = false
        color = TEXT_GRAY
    }
}


