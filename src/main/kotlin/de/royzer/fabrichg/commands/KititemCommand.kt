package de.royzer.fabrichg.commands

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.util.forceGiveItem
import net.minecraft.commands.arguments.EntityArgument
import net.silkmc.silk.commands.command

val kititemCommand = command("kititemfix") {
    requiresPermissionLevel(4)

    argument("player", EntityArgument.player()) { player ->
        runs {
            val serverPlayer = player(this).findPlayers(this.source).first() ?: return@runs
            val hgPlayer = serverPlayer.hgPlayer

            hgPlayer.kits.forEach { kit ->
                kit.kitItems.forEach {
                    if (!serverPlayer.inventory.contains(it.itemStack)) {
                        serverPlayer.forceGiveItem(it.itemStack)
                    }
                }
            }
        }
    }

}