package de.royzer.fabrichg.commands

import de.royzer.fabrichg.bots.HGBot
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.entity.pos
import net.silkmc.silk.core.entity.world

val hgbotCommand = command("hgbot") {
    requiresPermissionLevel(4)
    argument("name") { name ->
        runs {
            val world = source.player?.world
            source.player?.world?.addFreshEntity(HGBot(world!!, name(), source.player!!).apply {
                setPos(source!!.player!!.pos)
            })
        }
    }
    runs {
        val world = source.player?.world
        source.player?.world?.addFreshEntity(HGBot(world!!, "HGBot", source.player!!).apply {
            setPos(source!!.player!!.pos)
        })
    }
}