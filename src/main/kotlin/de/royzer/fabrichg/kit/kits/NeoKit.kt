package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.Kit
import de.royzer.fabrichg.kit.achievements.delegate.achievement
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.mixins.entity.ProjectileAccessor
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.entity.projectile.ThrownPotion
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

val neoKit: Kit = kit("Neo") {
    kitSelectorItem = ItemStack(Items.ARROW)
    description = "Block enemy kits and projectiles"

    kitEvents {
        onHitByProjectile { hgPlayer, kit, entityHitResult, projectileEntity ->
            if (entityHitResult.entity?.hgPlayer?.isNeo == true) {
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
                (projectileEntity as ProjectileAccessor).onHitEntityInvoker(entityHitResult)
            }
        }
    }
}

// TODO: das überall machen
val blockKitsAchievement by neoKit.achievement("block kits") {
    level(25)
    level(100)
    level(1000)
}