package de.royzer.fabrichg.kit.events

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.kit.Kit
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity

class KitEventsBuilder(val kit: Kit) {
    fun onHitPlayer(action: (HGPlayer, Kit, ServerPlayer) -> Unit) {
        kit.events.hitPlayerAction = action
    }
    fun onHitEntity(action: (HGPlayer, Kit, Entity) -> Unit) {
        kit.events.hitEntityAction = action
    }
    fun onMove(action: (HGPlayer, Kit) -> Unit) {
        kit.events.moveAction = action
    }
}