package de.royzer.fabrichg.commands

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.teams.*
import net.minecraft.commands.arguments.EntityArgument
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.text.sendText

// eig team aber der vanilla command ist da schon kein plan wie man den removed
val teamCommand = command("hgteam") {
    literal("create") {
        argument<String>("name") { _name ->
            runs {
                val player = source.player ?: return@runs
                val hgPlayer = player.hgPlayer
                if (hgPlayer.isInTeam) {
                    player.sendText("You are already in a team") { color = TEXT_GRAY }
                    return@runs
                }
                val name = _name()
                val team = HGTeam(name, hgPlayer)
                player.sendText() {
                    text("You successfully created the team ") { color = TEXT_GRAY }
                    text(name) {color = TEXT_BLUE}
                }
                teams.add(team)
            }
        }
    }

    literal("invite") {
        argument("player", EntityArgument.player()) { player ->
            runs {
                val inviter = source.player ?: return@runs
                val invitedPlayer = player(this).findPlayers(this.source).first() ?: return@runs
                val team: HGTeam = inviter.hgPlayer.hgTeam ?: return@runs
                invitedPlayer.sendText {
                    text("You were invited to the team ") { color = TEXT_GRAY }
                    text(team.name) { color = TEXT_BLUE }
                    text(" from ") { color = TEXT_GRAY }
                    text(inviter.name.string) { color = TEXT_BLUE }
                }
                inviter.sendText {
                    text("You invited ") { color = TEXT_GRAY }
                    text(invitedPlayer.name.string) { color = TEXT_BLUE }
                }
                pendingInvites[team]?.add(invitedPlayer.hgPlayer)
            }
        }
    }

    literal("join") {
        argument<String>("name") { name ->
            runs {
                val hgPlayer = source.player?.hgPlayer ?: return@runs
                if (hgPlayer.isInTeam) {
                    source.player?.sendText("You are already in a team") { color = TEXT_GRAY }
                    return@runs
                }
                val team = teams.find { it.name == name() } ?: kotlin.run {
                    source.player?.sendText("There is no team with that name") { color = TEXT_GRAY }
                    return@runs
                }
                if (pendingInvites[team]?.contains(hgPlayer) == true) {
                    val added = team.addPlayer(hgPlayer)
                    if (!added) { // wenn nicht added muss voll sein
                        source.player?.sendText("This team is full") { color = TEXT_GRAY }
                    }
                    pendingInvites[team]?.remove(hgPlayer)
                }
            }
        }
    }

    literal("leave") runs {
        val hgPlayer = source.player?.hgPlayer ?: return@runs
        val team = hgPlayer.hgTeam
        if (team != null) {
            hgPlayer.hgTeam?.removePlayer(hgPlayer)
        } else {
            source.player?.sendText {
                text("You are in no team") { color = TEXT_GRAY }
            }
        }
    }

    literal("delete") runs {
        val hgPlayer = source.player?.hgPlayer ?: return@runs
        val team = hgPlayer.hgTeam
        if (team?.leader == hgPlayer) {
            team.delete()
        } else {
            source.player?.sendText {
                text("You cannot delete this team") { color = TEXT_GRAY }
            }
        }
    }

    literal("info") runs {
        val team = source.player?.hgPlayer?.hgTeam
        source.player?.sendText {
            if (team != null) {
                text("You are in the team ") { color = TEXT_GRAY }
                text(team.name) { color = TEXT_BLUE }
                text(" with leader ") { color = TEXT_GRAY }
                text(team.leader.name) { color = TEXT_BLUE }
            } else {
                text("You are in no team") { color = TEXT_GRAY }
            }
        }
    }

    literal("list") runs {
        source.player?.sendText {
            teams.forEach {
                text(it.name + ": ") { color = TEXT_BLUE }
                text(it.hgPlayers.joinToString(", ") { it.name }) { color = TEXT_GRAY }
                text("\n")
            }
        }
    }
}