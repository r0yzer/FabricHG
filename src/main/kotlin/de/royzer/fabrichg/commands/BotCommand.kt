package de.royzer.fabrichg.commands

import de.royzer.fabrichg.bots.HGBot
import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.kit.kits.beerKit
import de.royzer.fabrichg.kit.randomKit
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.entity.pos
import net.silkmc.silk.core.entity.world

val hgbotCommand = command("hgbot") {
    requiresPermissionLevel(1)
    argument("name") { name ->
        runs {
            val botname = name()
            if (botname.toString().length <= 16) {
                val world = source.player?.world
                val hgBot = HGBot(world!!, botname, source.player!!)
                source.player?.world?.addFreshEntity(hgBot.apply {
                    setPos(source!!.player!!.pos)
                })
                PlayerList.players[hgBot.uuid] = HGPlayer(hgBot.uuid, botname)
                PlayerList.players[hgBot.uuid]?.kits?.add(randomKit())
            }

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