package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.kit
import net.minecraft.entity.Entity
import net.minecraft.entity.projectile.ExplosiveProjectileEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.hit.EntityHitResult
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

val neoKit = kit("Neo") {
    kitSelectorItem = ItemStack(Items.ARROW)
}

fun neoOnProjectileHit(entityHitResult: EntityHitResult, projectileEntity: ProjectileEntity, ci: CallbackInfo) {
    if ((entityHitResult.entity as? ServerPlayerEntity)?.hgPlayer?.canUseKit(neoKit) == false) return
    ci.cancel()
    val hitEntity = entityHitResult.entity
    val vel = projectileEntity.velocity
    projectileEntity.remove(Entity.RemovalReason.KILLED)
    entityHitResult.entity.world.spawnEntity(projectileEntity.type.create(hitEntity.world).apply {
        this?.velocity = vel.multiply(-1.0)
        this?.setPosition(hitEntity.pos.add(0.0, 0.5, 0.0))
    })
}