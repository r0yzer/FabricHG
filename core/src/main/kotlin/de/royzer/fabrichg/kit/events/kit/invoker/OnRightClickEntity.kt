package de.royzer.fabrichg.kit.events.kit.invoker

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.kit.events.kit.invokeKitAction
import net.minecraft.world.entity.Entity

fun onRightClickEntity(hgPlayer: HGPlayer, entity: Entity) {
    hgPlayer.kits.forEach { kit ->
        hgPlayer.invokeKitAction(kit) {
            kit.events.rightClickEntityAction?.invoke(hgPlayer, kit, entity)
        }
    }
}