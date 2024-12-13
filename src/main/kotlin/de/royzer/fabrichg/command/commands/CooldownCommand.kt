package de.royzer.fabrichg.command.commands

import de.royzer.fabrichg.command.sharedCommand
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.cooldown.Cooldown
import de.royzer.fabrichg.kit.cooldown.cooldownMap
import net.silkmc.silk.commands.command

val cooldownCommand = sharedCommand("cooldown") {
    requiresPermissionLevel(4)
    literal("skip") runs {
        val hgPlayer = source.player?.hgPlayer ?: return@runs
        hgPlayer.kits.forEach {
            cooldownMap.remove(Cooldown(hgPlayer, it))
        }
    }
}