package de.royzer.fabrichg.kit.info

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.kit.Kit
import net.minecraft.network.chat.Component

typealias InfoGenerator = (HGPlayer, Kit) -> Component?
