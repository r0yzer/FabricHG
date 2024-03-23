package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.kit
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.projectile.Snowball
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.FrostWalkerEnchantment
import net.silkmc.silk.core.entity.posUnder
import net.silkmc.silk.core.item.itemStack

val frozenKit = kit("Frozen") {
    kitSelectorItem = Items.PACKED_ICE.defaultInstance
    description = "Freeze water and enemies"

    kitItem {
        itemStack = itemStack(Items.SNOWBALL) { count = 16 }
    }

    kitEvents {
        onMove { hgPlayer, _ ->
            hgPlayer.serverPlayer?.let {
                FrostWalkerEnchantment.onEntityMoved(it, it.level(), it.posUnder, 3)
            }
        }

        onHitProjectile { entityHitResult, projectileEntity ->
            if (projectileEntity !is Snowball) return@onHitProjectile;
            val owner = projectileEntity.owner as? ServerPlayer ?: return@onHitProjectile

            val hitEntity = entityHitResult.entity
            if (owner == hitEntity) return@onHitProjectile

            hitEntity.hurt(owner.damageSources().playerAttack(owner), 0.1f)
            hitEntity.ticksFrozen = 20 * 10
        }
    }
}

