package de.royzer.fabrichg.kit.events.kit.invoker

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.events.kit.invokeKitAction
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
                kitItem.invokeKitItemAction(hgPlayer, kit) {
                    kitItem.hitEntityAction?.invoke(hgPlayer, kit, entity)
                }
                if (target is ServerPlayer) {
                    kitItem.invokeKitItemAction(hgPlayer, kit) {
                        kitItem.hitPlayerAction?.invoke(hgPlayer, kit, target)
                    }
                }
            }
        }
        // TODO das muss nochmal besser werden wenn mehr kits als das eber kit das machen wollen
        val ignoreEntityCooldown = kit.events.noCooldownActions.contains<Any?>(kit.events.hitEntityAction)
        hgPlayer.invokeKitAction(kit, sendCooldown = false, ignoreCooldown = ignoreEntityCooldown) {
            kit.events.hitEntityAction?.invoke(hgPlayer, kit, target)
        }
        val ignorePlayerCooldown = kit.events.noCooldownActions.contains<Any?>(kit.events.hitPlayerAction)
        hgPlayer.invokeKitAction(kit, sendCooldown = false, ignoreCooldown = ignorePlayerCooldown) {
            if (target is ServerPlayer) {
                kit.events.hitPlayerAction?.invoke(hgPlayer, kit, target)
            }
        }
    }
}