package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.Kit
import de.royzer.fabrichg.kit.kit
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.projectile.Snowball
import net.minecraft.world.item.Items
import net.silkmc.silk.core.entity.pos
import net.silkmc.silk.core.item.itemStack

val switcherKit: Kit = kit("Switcher") {
    kitSelectorItem = Items.SNOWBALL.defaultInstance

    description = "Switch yourself with someone else"

    kitItem {
        itemStack = itemStack(Items.SNOWBALL) { count = 16 }
    }

    kitEvents {
        onHitProjectile { hgPlayer, kit, entityHitResult, projectileEntity ->
            if (projectileEntity !is Snowball) return@onHitProjectile;

            val owner = projectileEntity.owner as? ServerPlayer ?: return@onHitProjectile
            val hitEntity = entityHitResult.entity

            if (owner == hitEntity) return@onHitProjectile

            val hitEntityPos = hitEntity.pos
            val ownerPos = owner.pos

            owner.teleportTo(hitEntityPos.x, hitEntityPos.y, hitEntityPos.z)
            hitEntity.teleportTo(ownerPos.x, ownerPos.y, ownerPos.z)
            hitEntity.hurt(owner.damageSources().playerAttack(owner), 0.1f)
        }
    }
}