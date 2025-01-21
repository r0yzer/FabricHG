package de.royzer.fabrichg.commands

import de.royzer.fabrichg.data.hgplayer.HGPlayerStatus
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.phase.PhaseType
import net.minecraft.world.level.GameType
import net.silkmc.silk.commands.command

val hgplayerRemoveCommand = command("hgplayerremove") {
    requiresPermissionLevel(4)
    argument<String>("name") { _name ->
        suggestsListFiltering {
            PlayerList.alivePlayers.map { it.name }
        }
        runs {
            val name = _name()
            val hgPlayer = PlayerList.players.values.find { it.name == name } ?: return@runs
            hgPlayer.status = HGPlayerStatus.SPECTATOR
            hgPlayer.serverPlayer?.setGameMode(GameType.SPECTATOR)
            hgPlayer.allKits.forEach {
                it.onDisable?.invoke(hgPlayer, it)
            }
        }
    }
}