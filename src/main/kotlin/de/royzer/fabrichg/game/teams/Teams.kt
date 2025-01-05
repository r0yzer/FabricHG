package de.royzer.fabrichg.game.teams

import de.royzer.fabrichg.data.hgplayer.HGPlayer

val teams = mutableListOf<HGTeam>()


val maxTeamSize = 3 // in gameconfig noch machen, aber nicht während der runde ändern lassen können (brauch man nie)
// und noch irgendwie ob teams enabled sind in gameconfig und dementsprechen command registrieren wenns geht etc

val pendingInvites = hashMapOf<HGTeam, MutableList<HGPlayer>>()

val HGPlayer.isInTeam: Boolean get() = teams.find { it.hgPlayers.contains(this) } != null

val HGPlayer.hgTeam get() = teams.find { it.hgPlayers.contains(this) }