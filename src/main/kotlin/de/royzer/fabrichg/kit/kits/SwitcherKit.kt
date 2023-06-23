package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.kit
import net.silkmc.silk.core.entity.pos
import net.silkmc.silk.core.item.itemStack
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageSources
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.entity.projectile.Snowball
import net.minecraft.world.item.Items
import net.minecraft.world.phys.EntityHitResult
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

val switcherKit = kit("Switcher") {
    kitSelectorItem = Items.SNOWBALL.defaultInstance

    kitItem {
        itemStack = itemStack(Items.SNOWBALL) { count = 16 }
    }
}

fun switcherOnEntityHit(entityHitResult: EntityHitResult, ci: CallbackInfo, snowballEntity: Snowball) {
    val owner = snowballEntity.owner as? ServerPlayer ?: return
    val hitEntity = entityHitResult.entity
    if (owner.hgPlayer.canUseKit(switcherKit)) {
        val hitEntityPos = hitEntity.pos
        val ownerPos = owner.pos
        owner.teleportTo(hitEntityPos.x, hitEntityPos.y, hitEntityPos.z)
        hitEntity.teleportTo(ownerPos.x, ownerPos.y, ownerPos.z)
        hitEntity.hurt(owner.damageSources().playerAttack(owner), 0.1f)
    }
}