package de.royzer.fabrichg.commands

import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.teams.hgTeam
import de.royzer.fabrichg.kit.kits.sendHGLaborChatMessage
import net.minecraft.commands.arguments.MessageArgument
import net.minecraft.server.level.ServerPlayer
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.text.sendText

fun ServerPlayer.sendTeamMessage(message: String) {
    val team = hgPlayer.hgTeam ?: run {
        sendText("You are not in a team") { color = TEXT_GRAY }
        return
    }

    team.hgPlayers.forEach { teamPlayer ->
        teamPlayer.serverPlayer?.sendHGLaborChatMessage(this, message, team)
    }
}

val teamChatCommand = command("tchat") {
    alias("teamchat")
    alias("güldemürchat")

    argument("message", MessageArgument.message()) { _message ->
        runs {
            val serverPlayer = source.player ?: return@runs
            val message = _message().text

            serverPlayer.sendTeamMessage(message)
        }
    }

    runs {
        val hgPlayer = source.player?.hgPlayer ?: return@runs

        hgPlayer.teamChat = !hgPlayer.teamChat
    }
}