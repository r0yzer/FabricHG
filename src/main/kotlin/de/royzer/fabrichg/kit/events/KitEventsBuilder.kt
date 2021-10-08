package de.royzer.fabrichg.kit.events

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.kit.Kit
import net.minecraft.entity.Entity
import net.minecraft.server.network.ServerPlayerEntity

class KitEventsBuilder(val kit: Kit) {
    fun onHitPlayer(action: (HGPlayer, Kit, ServerPlayerEntity) -> Unit) {
        kit.events.hitPlayerAction = action
    }
    fun onHitEntity(action: (HGPlayer, Kit, Entity) -> Unit) {
        kit.events.hitEntityAction = action
    }
}