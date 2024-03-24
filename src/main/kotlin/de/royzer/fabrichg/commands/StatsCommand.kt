package de.royzer.fabrichg.commands

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.server
import de.royzer.fabrichg.stats.Database
import de.royzer.fabrichg.stats.Stats
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.commands.arguments.GameProfileArgument
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.entity.player.Player
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.text.literalText
import net.silkmc.silk.core.text.sendText
import java.awt.Color
import javax.swing.text.html.parser.Entity

val statsCommand = command("stats"){
    fun getStatsMessage(playerName: String, stats: Stats): MutableComponent {
      return  literalText{
            text("-----------------")
            newLine()
            text(playerName)
            emptyLine()
            text("Kills: " + stats.kills.toString())
            newLine()
            text("Deaths: "+stats.deaths.toString())
            newLine()
            text("Wins: "+stats.wins.toString())
            newLine()
            text("-----------------")
        }
    }
    runs {
        val result = Database.getStatsForPlayer(this.source.player?.uuid!!)
        result.invokeOnCompletion {
            if(it == null){
                val stats = result.getCompleted()
                this.source.player!!.sendText(
                    getStatsMessage(this.source.player!!.name.string, stats)
                )
            }
        }
    }
    argument<String>("player"){ nameArg ->
        runs {
            val name = nameArg()
            val wasOnline = !(server.profileCache?.get(name)?.isEmpty)!!
            if(!wasOnline){
                this.source.player?.sendText(
                    literalText {
                        text("Der Spieler war noch nie online"){
                            color = 0xFF0000
                        }
                    }
                )
                return@runs
            }
            val cachedGameProfile = server.profileCache?.get(name)!!.get()
            val result = Database.getStatsForPlayer(cachedGameProfile.id)
            result.invokeOnCompletion {
                if(it == null){
                    val stats = result.getCompleted()
                    this.source.player!!.sendText(
                        getStatsMessage(cachedGameProfile.name, stats)
                    )
                }
            }

        }
    }
}