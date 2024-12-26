package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.achievements.delegate.achievement
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.projectile.Snowball
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.FrostedIceBlock
import net.silkmc.silk.core.entity.blockPos
import net.silkmc.silk.core.entity.pos
import net.silkmc.silk.core.entity.posUnder
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.math.geometry.produceFilledCirclePositions

val frozenKit = kit("Frozen") {
    kitSelectorItem = Items.PACKED_ICE.defaultInstance
    description = "Freeze water and enemies"

    val frozenDurationInS by property(10, "frozen duration in seconds")
    val frostWalkerLevel by property(3, "frost walker level")
    val snowballDamage by property(0.1f, "snowball damage")

    val freezeForSecondsAchievement by achievement("freeze players for seconds") {
        level(700)
        level(2000)
        level(5000)
    }
    val transformWaterAchievement by achievement("transform water to ice") {
        level(1000)
        level(10000)
        level(15000)
    }

    kitItem {
        itemStack = itemStack(Items.SNOWBALL) { count = 16 }
    }

    kitEvents {
        onMove { hgPlayer, _ ->
            hgPlayer.serverPlayer?.let { player ->

                if (player.onGround()) {
                    val posUnder = player.posUnder
                    val range = (frostWalkerLevel + 1) * 2
                    val level = player.level()

                    posUnder.produceFilledCirclePositions(range) {
                        if (level.getBlockState(it).block != Blocks.WATER) return@produceFilledCirclePositions

                        level.setBlockAndUpdate(BlockPos(it.x, it.y, it.z), Blocks.FROSTED_ICE.defaultBlockState())

                        transformWaterAchievement.awardLater(player)
                    }
                }
            }
        }

        onHitProjectile { hgPlayer, kit, entityHitResult, projectileEntity ->
            val player = hgPlayer.serverPlayer ?: return@onHitProjectile

            if (projectileEntity !is Snowball) return@onHitProjectile;
            val owner = projectileEntity.owner as? ServerPlayer ?: return@onHitProjectile

            val hitEntity = entityHitResult.entity
            if (owner == hitEntity) return@onHitProjectile

            if (hitEntity.hgPlayer?.isNeo == true) return@onHitProjectile

            hitEntity.hurt(owner.damageSources().playerAttack(owner), snowballDamage)
            hitEntity.ticksFrozen = 20 * frozenDurationInS

            freezeForSecondsAchievement.awardLater(player, frozenDurationInS)
        }
    }
}

