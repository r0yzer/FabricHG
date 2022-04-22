package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.kit
import net.axay.fabrik.core.entity.pos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.phys.EntityHitResult
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

val neoKit = kit("Neo") {
    kitSelectorItem = ItemStack(Items.ARROW)
}

fun neoOnProjectileHit(entityHitResult: EntityHitResult, projectileEntity: Projectile, ci: CallbackInfo) {
    if ((entityHitResult.entity as? ServerPlayer)?.hgPlayer?.canUseKit(neoKit) == false) return
    projectileEntity.remove(Entity.RemovalReason.KILLED)
    ci.cancel()
    val hitEntity = entityHitResult.entity
    val vel = projectileEntity.deltaMovement
    entityHitResult.entity.level.addFreshEntity(projectileEntity.type.create(hitEntity.level).apply {
        this?.deltaMovement = vel.reverse()
        this?.setPos(hitEntity.pos.add(0.0, 0.5, 0.0))
    }!!)
}