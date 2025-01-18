package de.royzer.fabrichg.game

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.TEXT_YELLOW_CHAT
import de.royzer.fabrichg.bots.HGBot
import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.data.hgplayer.HGPlayerStatus
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.game.phase.phases.IngamePhase
import de.royzer.fabrichg.gulag.GulagManager
import net.minecraft.network.chat.Component
import net.silkmc.silk.core.text.literalText
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.GameType
import net.silkmc.silk.core.text.sendText
import java.util.*

object PlayerList {
    val players = mutableMapOf<UUID, HGPlayer>()

    val aliveOrGulagPlayers get() = players.values.filter { it.status == HGPlayerStatus.ALIVE || it.status == HGPlayerStatus.DISCONNECTED || it.status == HGPlayerStatus.GULAG }
    val alivePlayers get() = players.values.filter { it.status == HGPlayerStatus.ALIVE || it.status == HGPlayerStatus.DISCONNECTED }

    val spectators get() = players.values.filter { it.status == HGPlayerStatus.SPECTATOR }

    val maxPlayers: Int
        get() = if (GamePhaseManager.currentPhaseType == PhaseType.INGAME || GamePhaseManager.currentPhaseType == PhaseType.END) IngamePhase.maxPlayers else alivePlayers.size

    fun addOrGetPlayer(uuid: UUID, name: String): HGPlayer {
        return players.getOrPut(uuid) {
            HGPlayer(uuid, name)
        }
    }

    fun getPlayer(uuid: UUID): HGPlayer? {
        return players[uuid]
    }

    fun removePlayer(uuid: UUID) {
        players.remove(uuid)
    }

    fun announcePlayerDeath(deadPlayer: HGPlayer, source: DamageSource, killer: Entity?, gulag: Boolean = false) {
        val sourceKiller = source.entity

        val deathMessage = literalText { "${deadPlayer.name}(${deadPlayer.kits.joinToString { it.name }})" } //Player(Kit)
        if(killer != null) {
            deathMessage.append(" was killed by ${killer.name.string}") //Player(Kit) was killed by Killer
            if(killer == sourceKiller && killer is ServerPlayer || killer is HGBot) {
                deathMessage.append("(${killer.hgPlayer?.kits?.joinToString { it.name }})") //Player(Kit) was killed by Killer(Kit)
                val itemName = (killer as LivingEntity).mainHandItem?.item.toString().uppercase()
                deathMessage.append(" using $itemName") //Player(Kit) was killed by Killer(Kit) using IRON_AXE
            }
        } else {
            //KA ob das simpel sein soll wie in der originalen impl, aber das hier wäre standard death message aber mit Player(Kit) statt Player
            deathMessage.siblings.removeLast()
            deathMessage.append(Component.translatable("death.attack.${source.msgId}.player", "${deadPlayer.name}(${deadPlayer.kits.joinToString { it.name }})"))
        }

        broadcastComponent(deathMessage.withColor(TEXT_YELLOW_CHAT))

        announceRemainingPlayers()
        if (alivePlayers.size < GulagManager.minPlayersOutsideGulag) {
            GulagManager.close()
        }
    }

    fun announceRemainingPlayers() {
        broadcastComponent(
            literalText {
                val players = alivePlayers.size
                text("$players player${if (players == 1) "" else "s"} remaining") {
                    color = TEXT_YELLOW_CHAT
                }
            }
        )
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


