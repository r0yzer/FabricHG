package de.royzer.fabrichg.kit.events.kit.invoker

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.kit.events.kit.invokeKitAction
import net.minecraft.world.entity.Entity

fun onRightClickEntity(hgPlayer: HGPlayer, entity: Entity) {
    hgPlayer.allKits.forEach { kit ->
        hgPlayer.invokeKitAction(kit, sendCooldown = kit.events.rightClickEntityAction != null) {
            kit.events.rightClickEntityAction?.invoke(hgPlayer, kit, entity)
        }
    }
}