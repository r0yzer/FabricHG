package de.royzer.fabrichg.kit.events.kit.invoker

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.events.kit.invokeKitAction
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

fun onAttackEntity(target: Entity, entity: LivingEntity, ci: CallbackInfo) {
    val attacker = entity.hgPlayer ?: return
    val item = entity.mainHandItem
    val offhandItem = entity.mainHandItem
    attacker.kits.forEach { kit ->
        kit.kitItems.forEach { kitItem ->
            if (kitItem.itemStack.item == item.item || offhandItem.item == kitItem.itemStack.item) {
                kitItem.invokeKitItemAction(attacker, kit) {
                    kitItem.hitEntityAction?.invoke(attacker, kit, entity)
                }
                if (target is ServerPlayer) {
                    kitItem.invokeKitItemAction(attacker, kit) {
                        kitItem.hitPlayerAction?.invoke(attacker, kit, target)
                    }
                }
            }
        }
        // TODO das muss nochmal besser werden wenn mehr kits als das eber kit das machen wollen
        val ignoreEntityCooldown = kit.events.noCooldownActions.contains<Any?>(kit.events.hitEntityAction)
        attacker.invokeKitAction(kit, sendCooldown = false, ignoreCooldown = ignoreEntityCooldown) {
            kit.events.hitEntityAction?.invoke(attacker, kit, target)
        }
        val ignorePlayerCooldown = kit.events.noCooldownActions.contains<Any?>(kit.events.hitPlayerAction)
        attacker.invokeKitAction(kit, sendCooldown = false, ignoreCooldown = ignorePlayerCooldown) {
            if (target is ServerPlayer) {
                kit.events.hitPlayerAction?.invoke(attacker, kit, target)
            }
        }
    }
    if (target is ServerPlayer) {
        target.hgPlayer.kits.forEach { kit ->
            target.hgPlayer.invokeKitAction(kit, sendCooldown = false) {
                if (attacker.serverPlayer != null) {
                    val shouldCancel = kit.events.attackedByPlayerAction?.invoke(target.hgPlayer, kit, attacker.serverPlayer!!)
                    if (shouldCancel == true) {
                        ci.cancel()
                    }
                }
            }
        }

    }
}


fun afterAttackEntity(target: Entity, entity: LivingEntity) {
    val hgPlayer = entity.hgPlayer ?: return

    hgPlayer.kits.forEach { kit ->
        // TODO das muss nochmal besser werden wenn mehr kits als das eber kit das machen wollen
        val ignoreEntityCooldown = kit.events.noCooldownActions.contains<Any?>(kit.events.afterHitEntityAction)
        hgPlayer.invokeKitAction(kit, sendCooldown = false, ignoreCooldown = ignoreEntityCooldown) {
            kit.events.afterHitEntityAction?.invoke(hgPlayer, kit, target)
        }
    }
}