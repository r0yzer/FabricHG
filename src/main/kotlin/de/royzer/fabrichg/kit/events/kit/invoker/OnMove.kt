package de.royzer.fabrichg.kit.events.kit.invoker

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.events.kit.invokeKitAction
import net.minecraft.world.entity.Entity


fun onMove(entity: Entity) {
    val hgPlayer = entity.hgPlayer ?: return
    hgPlayer.allKits.forEach { kit ->
        hgPlayer.invokeKitAction(kit, sendCooldown = false) {
            kit.events.moveAction?.invoke(hgPlayer, kit)
        }
    }
}