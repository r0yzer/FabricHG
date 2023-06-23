package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.mixins.entity.LivingEntityAccessor
import net.silkmc.silk.core.entity.modifyVelocity
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.phys.Vec3
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

val anchorKit = kit("Anchor") {
    kitSelectorItem = ItemStack(Items.ANVIL)
}

fun onAnchorAttackEntity(target: Entity, serverPlayerEntity: ServerPlayer) {
    if (serverPlayerEntity.hgPlayer.canUseKit(anchorKit)) {
        if (target is ServerPlayer) {
            if ((target as? ServerPlayer)?.hgPlayer?.canUseKit(neoKit, true) == true) return
        }
        target.modifyVelocity(0, 0, 0, false)
        target.modifyVelocity(0,-0.1,0, false)
    }
}

fun onAnchorKnockback(strength: Double, x: Double, z: Double, ci: CallbackInfo, livingEntity: LivingEntity) {
    val serverPlayerEntity = livingEntity as? ServerPlayer ?: return
    if (serverPlayerEntity.hgPlayer.canUseKit(anchorKit)) {
        if ((((serverPlayerEntity as LivingEntityAccessor).attackingPlayer) as? ServerPlayer)?.hgPlayer?.hasKit(neoKit) == true) return
        ci.cancel()
        serverPlayerEntity.deltaMovement = Vec3.ZERO
//        serverPlayerEntity.modifyVelocity(0,-0.1,0, false)
    }
}