package de.royzer.fabrichg.commands

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.cooldown.Cooldown
import de.royzer.fabrichg.kit.cooldown.cooldownMap
import net.silkmc.silk.commands.command

val cooldownCommand = command("cooldown") {
    requiresPermissionLevel(4)
    literal("skip") runs {
        val hgPlayer = source.player?.hgPlayer ?: return@runs
        hgPlayer.allKits.forEach {
            cooldownMap.remove(Cooldown(hgPlayer, it))
        }
    }
}