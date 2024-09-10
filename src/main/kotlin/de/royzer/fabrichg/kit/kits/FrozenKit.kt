package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.projectile.Snowball
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantments
import net.silkmc.silk.core.entity.blockPos
import net.silkmc.silk.core.entity.pos
import net.silkmc.silk.core.entity.posUnder
import net.silkmc.silk.core.item.itemStack

val frozenKit = kit("Frozen") {
    kitSelectorItem = Items.PACKED_ICE.defaultInstance
    description = "Freeze water and enemies"

    val frozenDurationInS by property(10, "frozen duration in seconds")
    val frostWalkerLevel by property(3, "frost walker level")
    val snowballDamage by property(0.1f, "snowball damage")

    kitItem {
        itemStack = itemStack(Items.SNOWBALL) { count = 16 }
    }

    kitEvents {
//        onMove { hgPlayer, _ ->
//            hgPlayer.serverPlayer?.let {
///
//                FrostWalkerEnchantment.onEntityMoved(it, it.level(), it.blockPos, frostWalkerLevel)
//            }
//        }

        onHitProjectile { hgPlayer, kit, entityHitResult, projectileEntity ->
            if (projectileEntity !is Snowball) return@onHitProjectile;
            val owner = projectileEntity.owner as? ServerPlayer ?: return@onHitProjectile

            val hitEntity = entityHitResult.entity
            if (owner == hitEntity) return@onHitProjectile

            hitEntity.hurt(owner.damageSources().playerAttack(owner), snowballDamage)
            hitEntity.ticksFrozen = 20 * frozenDurationInS
        }
    }
}

