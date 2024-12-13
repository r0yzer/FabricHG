package de.royzer.fabrichg.command.commands

import de.royzer.fabrichg.command.hgCommand
import de.royzer.fabrichg.feast.Minifeast
import de.royzer.fabrichg.util.toHighestPos
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.entity.blockPos

val minifeastCommand = hgCommand("minifeast") {
    literal("spawn") {
        requiresPermissionLevel(4)

        runs {
            val minifeast = source.player?.blockPos?.let { Minifeast(it.toHighestPos()) }
            minifeast?.start()
        }
    }
}