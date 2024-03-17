package de.royzer.fabrichg.commands

import de.royzer.fabrichg.bots.HGBot
import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.kit.kits.beerKit
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.entity.pos
import net.silkmc.silk.core.entity.world

val hgbotCommand = command("hgbot") {
    requiresPermissionLevel(4)
    argument("name") { name ->
        runs {
            val world = source.player?.world
            val hgBot = HGBot(world!!, name(), source.player!!)
            source.player?.world?.addFreshEntity(hgBot.apply {
                setPos(source!!.player!!.pos)
            })
            PlayerList.players[hgBot.uuid] = HGPlayer(hgBot.uuid, name())
            PlayerList.players[hgBot.uuid]?.kits?.add(beerKit)
        }
    }
    runs {
        val world = source.player?.world
        val hgBot = HGBot(world!!, "HGBot", source.player!!)
        source.player?.world?.addFreshEntity(hgBot.apply {
            setPos(source!!.player!!.pos)
        })
        PlayerList.players[hgBot.uuid] = HGPlayer(hgBot.uuid, "HGBot")
        PlayerList.players[hgBot.uuid]?.kits?.add(beerKit)
    }
}