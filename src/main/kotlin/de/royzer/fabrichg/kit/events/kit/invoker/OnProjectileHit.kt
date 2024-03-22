package de.royzer.fabrichg.kit.events.kit.invoker

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.events.kit.invokeKitAction
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.phys.EntityHitResult

fun onProjectileHit(entityHitResult: EntityHitResult, projectileEntity: Projectile) {
    val hitter = projectileEntity.owner?.hgPlayer
    val hit = entityHitResult.entity?.hgPlayer
    hitter?.hitWithProjectile(entityHitResult, projectileEntity)
    hit?.hitByProjectile(entityHitResult, projectileEntity)
}

private fun HGPlayer.hitWithProjectile(entityHitResult: EntityHitResult, projectileEntity: Projectile) {
    kits.forEach { kit ->
        invokeKitAction(kit, sendCooldown = false) {
            kit.events.projectileHitAction?.invoke(entityHitResult, projectileEntity)
        }
    }
}

private fun HGPlayer.hitByProjectile(entityHitResult: EntityHitResult, projectileEntity: Projectile) {
    kits.forEach { kit ->
        invokeKitAction(kit, sendCooldown = false) {
            kit.events.projectileHitByAction?.invoke(entityHitResult, projectileEntity)
        }
    }
}