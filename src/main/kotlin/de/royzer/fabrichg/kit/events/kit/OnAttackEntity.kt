package de.royzer.fabrichg.kit.events.kit

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.isKitItem
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity

fun onAttackEntity(target: Entity, entity: LivingEntity) {
    val hgPlayer = entity.hgPlayer ?: return
    val item = entity.mainHandItem
    val offhandItem = entity.mainHandItem
    hgPlayer.kits.forEach { kit ->
        kit.kitItems.forEach { kitItem ->
            if (kitItem.itemStack.item == item.item || offhandItem.item == kitItem.itemStack.item) {
                kitItem.invokeHitEntityAction(hgPlayer, kit, target)
                if (target is ServerPlayer) {
                    kitItem.invokeHitPlayerAction(hgPlayer, kit, target)
                }
            }
        }
        if (hgPlayer.canUseKit(kit)) {
            kit.events.hitEntityAction?.invoke(hgPlayer, kit, target)
            if (target is ServerPlayer) {
                kit.events.hitPlayerAction?.invoke(hgPlayer, kit, target)
            }
        }
    }
}