package de.royzer.fabrichg.commands

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.data.hgplayer.HGPlayerStatus
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.broadcastComponent
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.util.forceGiveItem
import de.royzer.fabrichg.util.getRandomHighestPos
import de.royzer.fabrichg.util.toVec3
import de.royzer.fabrichg.util.tracker
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.level.GameType
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.text.literalText
import net.silkmc.silk.core.text.sendText

val reviveCommand = command("revive") {
    requiresPermissionLevel(4)
    requires {
        GamePhaseManager.currentPhaseType == PhaseType.INGAME
    }
    argument("player", EntityArgument.player()) { player ->
        runs {
            val serverPlayer = player(this).findPlayers(this.source).first() ?: return@runs
            val executor = source.player ?: return@runs
            val hgPlayer = serverPlayer.hgPlayer

            if (hgPlayer.status == HGPlayerStatus.ALIVE) {
                this.source.player?.sendText {
                    text("This player is still alive") {
                        color = TEXT_GRAY
                    }
                }
                return@runs
            }
            if (hgPlayer.status == HGPlayerStatus.GULAG) {
                this.source.player?.sendText {
                    text("This player is still in the gulag") {
                        color = TEXT_GRAY
                    }
                }
                return@runs
            }

            hgPlayer.revive(executor = executor)

        }

    }
}

fun HGPlayer.revive(executor: ServerPlayer? = null, gambler: Boolean = false) {
    val serverPlayer = this.serverPlayer ?: return
    val hgPlayer = this // kein bock das umzuschreiben

    if (hgPlayer.status != HGPlayerStatus.SPECTATOR) {
        return
    }
    hgPlayer.status = HGPlayerStatus.ALIVE


    serverPlayer.health = 20f
    serverPlayer.foodData.foodLevel = 20
    serverPlayer.setGameMode(GameType.SURVIVAL)
    if (executor != null) {
        serverPlayer.teleportTo(executor.x, executor.y, executor.z)
    } else {
        val randomHighestPos = getRandomHighestPos(200).toVec3()
        serverPlayer.teleportTo(randomHighestPos.x, randomHighestPos.y, randomHighestPos.z)
    }
    serverPlayer.attributes.getInstance(Attributes.ATTACK_SPEED)?.baseValue = 550.0
    serverPlayer.inventory.clearContent()
    serverPlayer.inventory.add(tracker)
    hgPlayer.kitsDisabled = false
    hgPlayer.kits.forEach {
        it.onEnable?.invoke(hgPlayer, it, serverPlayer)
        it.kitItems.forEach { item ->
            serverPlayer.forceGiveItem(item.itemStack.copy())
        }
    }

    broadcastComponent(literalText {
        text(serverPlayer.name.string) {
            color = TEXT_BLUE
            bold = true
        }
        text(" was ${if (gambler) "randomly " else ""}revived by ") {
            color = TEXT_GRAY
        }
        if (executor != null) {
            text(executor.name.string.toString()) {
                color = TEXT_BLUE
                bold = true
            }
        } else {
            text("a Gambler") {
                color = TEXT_BLUE
                bold = true
            }
        }

    })
}