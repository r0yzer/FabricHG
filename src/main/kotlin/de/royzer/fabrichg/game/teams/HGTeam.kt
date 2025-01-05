package de.royzer.fabrichg.game.teams

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.server
import net.minecraft.world.scores.PlayerTeam
import net.minecraft.world.scores.Scoreboard
import net.silkmc.silk.core.text.literal
import net.silkmc.silk.core.text.sendText

class HGTeam(
    val name: String,
    var leader: HGPlayer,
//    val playerTeam: PlayerTeam = PlayerTeam(server.scoreboard, name),
//    val scoreboard: Scoreboard = server.scoreboard
    ) {

    val hgPlayers: MutableList<HGPlayer> = mutableListOf()

    init {
        hgPlayers.add(leader)
        server.scoreboard.addPlayerTeam(name)
//        playerTeam.playerPrefix = "$name | ".literal
//        playerTeam.isAllowFriendlyFire = false
//        playerTeam.setSeeFriendlyInvisibles(true)
//        scoreboard.addPlayerToTeam(leader.name, playerTeam)
        pendingInvites[this] = mutableListOf()
    }

    /**
     * Prüft ob team noch Platz hat und sendet auch Nachrichten entsprechend
     * brauch man also nur aufrufen
     */
    fun addPlayer(hgPlayer: HGPlayer): Boolean {
        if (hgPlayers.size < maxTeamSize) {
            hgPlayers.forEach {
                it.serverPlayer?.sendText {
                    text(hgPlayer.name) { color = TEXT_BLUE }
                    text(" joined your team") { color = TEXT_GRAY }
                }
            }
            hgPlayers.add(hgPlayer)
//            scoreboard.addPlayerToTeam(hgPlayer.name, playerTeam)
            hgPlayer.serverPlayer?.sendText {
                text("You joined the team ") { color = TEXT_GRAY }
                text(name) { color = TEXT_BLUE }
            }

            return true
        }
        return false
    }

    fun removePlayer(hgPlayer: HGPlayer) {
        hgPlayers.remove(hgPlayer)
//        scoreboard.removePlayerFromTeam(name, playerTeam)
        hgPlayer.serverPlayer?.sendText {
            text("You left the team ") { color = TEXT_GRAY }
            text(name) { color = TEXT_BLUE }
        }

        hgPlayers.forEach {
            it.serverPlayer?.sendText {
                text(hgPlayer.name) { color = TEXT_BLUE }
                text(" left your team") { color = TEXT_GRAY }
            }
        }

        if (hgPlayers.size == 0) {
            delete()
        }

        if (hgPlayer == leader) {
            leader = hgPlayers.random()
        }
    }

    fun delete() {
        hgPlayers.forEach {
            it.serverPlayer?.sendText {
                text("Your team has been abandoned") { color = TEXT_GRAY }
            }
        }
        hgPlayers.clear()
        teams.remove(this)
//        scoreboard.removePlayerTeam(playerTeam)
        pendingInvites.remove(this)
    }

}