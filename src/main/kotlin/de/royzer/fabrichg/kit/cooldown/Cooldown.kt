package de.royzer.fabrichg.kit.cooldown

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.kit.Kit

data class Cooldown(
    val hgPlayer: HGPlayer,
    val kit: Kit,
)
