package de.royzer.fabrichg.commands

import de.royzer.fabrichg.bots.HGBot
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.entity.pos
import net.silkmc.silk.core.entity.world

val botCommand = command("bot") {
    requiresPermissionLevel(4)
    runs {
        val world = source.player?.world
        source.player?.world?.addFreshEntity(HGBot(world!!, "fler", source.player!!).apply {
            setPos(source!!.player!!.pos)
        })
    }
}