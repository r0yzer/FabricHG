package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.mixins.entity.ProjectileAccessor
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.projectile.Arrow
import net.minecraft.world.entity.projectile.Fireball
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.entity.projectile.ThrownPotion
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.phys.EntityHitResult
import net.silkmc.silk.core.logging.logInfo

val neoKit = kit("Neo") {
    kitSelectorItem = ItemStack(Items.ARROW)
}

// no other kit needs onHitByProjectile so no kit event
fun neoOnProjectileHit(entityHitResult: EntityHitResult, projectileEntity: Projectile) {
    if ((entityHitResult.entity as? ServerPlayer)?.hgPlayer?.isNeo == true) {
        projectileEntity.teleportTo(187.0, -50.0, 510.0) // out of map
        projectileEntity.remove(Entity.RemovalReason.KILLED)
        val hitEntity = entityHitResult.entity
        val vel = projectileEntity.deltaMovement
        entityHitResult.entity.level().addFreshEntity(projectileEntity.type.create(hitEntity.level()).apply {
            this?.deltaMovement = vel.reverse()
            this?.setPos(hitEntity.eyePosition)
            (this as? Projectile)?.owner = hitEntity
            if (projectileEntity is ThrownPotion) {
                (this as ThrownPotion).item = projectileEntity.item
            }
        }!!)
    } else {
        (projectileEntity as ProjectileAccessor).onHitEntityNeo(entityHitResult)
    }
}