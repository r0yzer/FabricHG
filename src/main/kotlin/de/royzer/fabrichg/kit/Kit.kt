package de.royzer.fabrichg.kit

import de.royzer.fabrichg.data.hgplayer.HGPlayer

abstract class Kit

fun HGPlayer.hasKit(kit: Kit) = kit in kits