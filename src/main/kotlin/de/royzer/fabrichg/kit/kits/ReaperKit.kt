package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.cooldown.checkUsesForCooldown
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.WitherSkull
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.Level.ExplosionInteraction
import net.minecraft.world.phys.HitResult
import net.silkmc.silk.core.entity.pos
import net.silkmc.silk.core.entity.world
import net.silkmc.silk.core.math.vector.plus
import kotlin.random.Random

class ReaperProjectile(world: Level, val explosion: Double = 1.25): WitherSkull(EntityType.WITHER_SKULL, world) {
    override fun onHit(result: HitResult) {
        super.onHit(result)
        level().explode(
            this,
            this.x,
            this.y,
            this.z,
            Random.nextDouble(explosion-0.5, explosion+0.5).toFloat(),
            false,
            ExplosionInteraction.MOB
        )
    }
}

val reaperKit = kit("Reaper") {
    kitSelectorItem = Items.NETHERITE_HOE.defaultInstance
    description = "You are the reaper"

    cooldown = 12.5

    val maxUses by property(3, "max uses")
    val maxUsesHitting by property(2, "max uses (hitting)")
    val explosion by property(1.25, "explosion grösse")
    val velocity by property(3.0, "velocity")
    val witherDuration by property(7, "wither duration (seconds, hit)")
    val witherLevel by property(2, "wither level")

    kitItem {
        itemStack = kitSelectorItem.copy()

        onClickAtEntity { hgPlayer, kit, entity, interactionHand ->
            if (entity !is LivingEntity) return@onClickAtEntity

            entity.addEffect(MobEffectInstance(MobEffects.WITHER, 20 * witherDuration, witherLevel))

            hgPlayer.checkUsesForCooldown(kit, maxUsesHitting)
        }

        onClick { hgPlayer, kit ->
            val world = hgPlayer.serverPlayer?.world ?: return@onClick

            world.addFreshEntity(ReaperProjectile(world, explosion).also {
                val lookVector = hgPlayer.serverPlayer!!.forward

                it.setPos(hgPlayer.serverPlayer!!.pos.plus(lookVector.multiply(1.5, 1.0, 1.5)))

                it.shoot(
                    lookVector.x,
                    lookVector.y,
                    lookVector.z,
                    velocity.toFloat(),
                    0.25f
                )

                hgPlayer.checkUsesForCooldown(kit, maxUses)
            })
        }
    }
}