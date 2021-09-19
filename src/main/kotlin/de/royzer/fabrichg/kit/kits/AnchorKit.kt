package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.Kit
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.mixins.entity.LivingEntityAccessor
import de.royzer.fabrichg.mixins.entity.damage.DamageTrackerAccessor
import net.axay.fabrik.core.math.vector.modifyVelocity
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

val anchorKit = kit("Anchor") {
    kitSelectorItem = ItemStack(Items.ANVIL)
}

fun onAnchorAttackEntity(target: Entity, serverPlayerEntity: ServerPlayerEntity) {
    if (serverPlayerEntity.hgPlayer.canUseKit(anchorKit)) {
        if (target is ServerPlayerEntity) {
            if ((target as? ServerPlayerEntity)?.hgPlayer?.canUseKit(neoKit, true) == true) return
        }
        target.setVelocity(0.0,0.0,0.0)
        target.modifyVelocity(0,-0.2,0, false)
    }
}

fun onAnchorKnockback(strength: Double, x: Double, z: Double, ci: CallbackInfo, livingEntity: LivingEntity) {
    val serverPlayerEntity = livingEntity as? ServerPlayerEntity ?: return
    if (serverPlayerEntity.hgPlayer.canUseKit(anchorKit)) {
        if ((((serverPlayerEntity as LivingEntityAccessor).attackingPlayer) as? ServerPlayerEntity)?.hgPlayer?.hasKit(neoKit) == true) return
        ci.cancel()
        serverPlayerEntity.setVelocity(0.0,0.0,0.0)
        serverPlayerEntity.modifyVelocity(0,-0.2,0, false)
    }
}