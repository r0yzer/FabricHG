package de.royzer.fabrichg.kit.events.kit.invoker

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.kit.cooldown.hasCooldown
import de.royzer.fabrichg.kit.cooldown.sendCooldown
import net.minecraft.world.entity.Entity

fun onRightClickEntity(hgPlayer: HGPlayer, entity: Entity) {
    hgPlayer.kits.forEach { kit ->
        if (hgPlayer.canUseKit(kit)) {
            kit.events.rightClickEntityAction?.invoke(hgPlayer, kit, entity)
        } else if (hgPlayer.hasCooldown(kit)) {
            hgPlayer.sendCooldown(kit)
        }
    }
}