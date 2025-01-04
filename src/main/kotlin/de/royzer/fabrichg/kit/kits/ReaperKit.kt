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

class ReaperProjectile(world: Level, val explosion: Double = 1.25, val dangerousChance: Int): WitherSkull(EntityType.WITHER_SKULL, world) {
    init {
        if (Random.nextInt(100) <= dangerousChance) isDangerous = true
    }

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

    override fun getInertia(): Float {
        return 1.0f
    }
}

val reaperKit = kit("Reaper") {
    kitSelectorItem = Items.NETHERITE_HOE.defaultInstance
    description = "You are the reaper"

    cooldown = 12.5

    maxUses = 3
    alternativeMaxUses = 2

    val explosion by property(1.25f, "explosion grösse")
    val velocity by property(3.0f, "velocity")
    val witherDuration by property(7, "wither duration (seconds, hit)")
    val witherLevel by property(2, "wither level")
    val dangerousChance by property(5, "dangerous chance (5%)")

    kitItem {
        itemStack = kitSelectorItem.copy()

        onClickAtEntity { hgPlayer, kit, entity, interactionHand ->
            if (entity !is LivingEntity) return@onClickAtEntity

            entity.addEffect(MobEffectInstance(MobEffects.WITHER, 20 * witherDuration, witherLevel))

            hgPlayer.checkUsesForCooldown(kit, alternativeMaxUses!!)
        }

        onClick { hgPlayer, kit ->
            val world = hgPlayer.serverPlayer?.world ?: return@onClick

            world.addFreshEntity(ReaperProjectile(world, explosion.toDouble(), dangerousChance).also {
                val lookVector = hgPlayer.serverPlayer!!.forward

                it.setPos(hgPlayer.serverPlayer!!.pos.plus(lookVector.multiply(1.5, 1.0, 1.5)))

                it.shoot(
                    lookVector.x,
                    lookVector.y,
                    lookVector.z,
                    velocity,
                    0.25f
                )

                hgPlayer.checkUsesForCooldown(kit, maxUses!!)
            })
        }
    }
}