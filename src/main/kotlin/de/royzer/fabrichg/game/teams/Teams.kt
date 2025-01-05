package de.royzer.fabrichg.game.teams

import de.royzer.fabrichg.data.hgplayer.HGPlayer

val teams = mutableListOf<HGTeam>()

val pendingInvites = hashMapOf<HGTeam, MutableList<HGPlayer>>()

val HGPlayer.isInTeam: Boolean get() = teams.find { it.hgPlayers.contains(this) } != null

val HGPlayer.hgTeam get() = teams.find { it.hgPlayers.contains(this) }