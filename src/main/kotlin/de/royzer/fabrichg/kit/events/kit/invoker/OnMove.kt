package de.royzer.fabrichg.kit.events.kit.invoker

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import net.minecraft.world.entity.Entity


fun onMove(entity: Entity) {
    val hgPlayer = entity.hgPlayer ?: return
    hgPlayer.kits.forEach { kit ->
        if (hgPlayer.canUseKit(kit)) {
            kit.events.moveAction?.invoke(hgPlayer, kit)
        }
    }
}