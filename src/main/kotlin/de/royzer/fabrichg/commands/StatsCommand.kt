package de.royzer.fabrichg.commands

import com.mojang.brigadier.context.CommandContext
import de.royzer.fabrichg.*
import de.royzer.fabrichg.stats.Stats
import de.royzer.fabrichg.util.round
import de.royzer.fabrichg.util.toStringWithoutTrailing0s
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.MutableComponent
import net.silkmc.silk.commands.ArgumentCommandBuilder
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.text.literalText
import net.silkmc.silk.core.text.sendText

fun <T> ArgumentCommandBuilder<CommandSourceStack, T>.suggestsListFiltering(block: CommandContext<CommandSourceStack>.() -> List<T>) {
    val umlaute: List<Char> = listOf(' ', 'ä', 'ü', 'ö', 'ß')
    suggestList { context ->
        val filtered = context.block().filter { listItem -> listItem.toString().contains(context.input.split(" ").last(), true) }
        filtered.map {
            val string = it.toString()
            if (string.any { umlaute.contains(it) }) """"$string"""" else string
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
val statsCommand = command("stats") {
    fun getStatsMessage(playerName: String, stats: Stats, allStats: Iterable<Stats>): MutableComponent {
        // ich erscheiss mich was ist das für formatting

        // wieso gibt es nicht Iterable#size
        var allSize = 0
        // ich scheiss mich ein das dauert 100% lange wenn da mehr als 3 spieler sind aber egal
        val worseThan = allStats.count { allSize++; it.score > stats.score }

        return literalText {
            text("-----------------") { color = TEXT_LIGHT_GRAY }
            newLine()
            text(playerName) { color = TEXT_BLUE }
            emptyLine()

            text("Kills: ") { color = TEXT_LIGHT_GRAY }
            text(stats.kills.toString()) { color = TEXT_YELLOW }
            newLine()

            text("Deaths: ") { color = TEXT_LIGHT_GRAY }
            text(stats.deaths.toString()) { color = TEXT_YELLOW }
            newLine()

            text("K/D: ") { color = TEXT_LIGHT_GRAY }
            val kd = stats.kills.toDouble() / stats.deaths.toDouble()
            text(String.format("%.2f", kd)) { color = TEXT_YELLOW }
            newLine()

            text("Wins: ") { color = TEXT_LIGHT_GRAY }
            text(stats.wins.toString()) { color = TEXT_YELLOW }
            newLine()

            text("Score: ") { color = TEXT_LIGHT_GRAY }
            text(stats.score.toString()) { color = TEXT_YELLOW }
            emptyLine()

            text("Platz: ") { color = TEXT_LIGHT_GRAY }
            text((worseThan + 1).toString()) { color = TEXT_BRIGHT_YELLOW }
            newLine()

            text("-----------------") { color = TEXT_LIGHT_GRAY }
        }
    }


    runsAsync {
        val playerResult = Stats.get(source.playerOrException)
        val allResults = Stats.getAll()

        try {
            playerResult.await()
            allResults.await()

            val completedPlayerStats = playerResult.getCompleted()
            val completedAllStats = allResults.getCompleted()

            val statsMessage = getStatsMessage(source.playerOrException.name.string, completedPlayerStats, completedAllStats)

            source.playerOrException.sendSystemMessage(statsMessage)
        } catch (e: Exception) {
            println("stats command fehler: $e")
            e.printStackTrace()
        }
    }

    argument<String>("player"){ nameArg ->
        suggestsListFiltering {
            source.server.playerNames.toList()
        }

        runsAsync {
            val name = nameArg()
            val wasOnline = !(server.profileCache?.get(name)?.isEmpty)!!
            if (!wasOnline) {
                this.source.player?.sendText(
                    literalText {
                        text("Der Spieler war noch nie online"){
                            color = 0xFF0000
                        }
                    }
                )
                return@runsAsync
            }

            val cachedGameProfile = server.profileCache?.get(name)!!.get()

            val playerResult = Stats.get(cachedGameProfile.id)
            val allResults = Stats.getAll()

            try {
                playerResult.await()
                allResults.await()

                val completedPlayerStats = playerResult.getCompleted()
                val completedAllStats = allResults.getCompleted()

                val statsMessage = getStatsMessage(cachedGameProfile.name, completedPlayerStats, completedAllStats)

                source.playerOrException.sendSystemMessage(statsMessage)
            } catch (e: Exception) {
                println("stats command fehler: $e")
                e.printStackTrace()
            }
        }
    }
}
