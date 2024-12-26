package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.Kit
import de.royzer.fabrichg.kit.achievements.delegate.achievement
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.projectile.Snowball
import net.minecraft.world.item.Items
import net.silkmc.silk.core.entity.pos
import net.silkmc.silk.core.item.itemStack

val switcherKit: Kit = kit("Switcher") {
    kitSelectorItem = Items.SNOWBALL.defaultInstance

    description = "Switch yourself with someone else"

    val snowballDamage by property(0.1f, "snowball damage")

    val switchTimesAchievement by achievement("switch") {
        level(35)
        level(100)
        level(500)
    }

    kitItem {
        itemStack = itemStack(Items.SNOWBALL) { count = 16 }
    }

    kitEvents {
        onHitProjectile { hgPlayer, kit, entityHitResult, projectileEntity ->
            if (projectileEntity !is Snowball) return@onHitProjectile;
            val player = hgPlayer.serverPlayer ?: return@onHitProjectile

            val owner = projectileEntity.owner as? ServerPlayer ?: return@onHitProjectile
            val hitEntity = entityHitResult.entity

            if (hitEntity.hgPlayer?.isNeo == true) {
                blockKitsAchievement.awardLater(hitEntity.hgPlayer?.serverPlayer ?: return@onHitProjectile)
                return@onHitProjectile
            }

            if (owner == hitEntity) return@onHitProjectile

            val hitEntityPos = hitEntity.pos
            val ownerPos = owner.pos

            owner.teleportTo(hitEntityPos.x, hitEntityPos.y, hitEntityPos.z)
            hitEntity.teleportTo(ownerPos.x, ownerPos.y, ownerPos.z)
            hitEntity.hurt(owner.damageSources().playerAttack(owner), snowballDamage)

            switchTimesAchievement.awardLater(player)
        }
    }
}