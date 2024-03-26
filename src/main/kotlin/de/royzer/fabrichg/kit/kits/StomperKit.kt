package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.item.Items
import net.silkmc.silk.core.entity.posUnder
import net.silkmc.silk.core.entity.world

val stomperKit = kit("Stomper") {
    kitSelectorItem = Items.DIAMOND_BOOTS.defaultInstance

    val range by property(7.5, "stomper range")

    kitEvents {
        onTakeDamage { player, source, amount ->
            if (!source.`is`(DamageTypes.FALL)) return@onTakeDamage amount

            val world = player.serverPlayer?.world ?: return@onTakeDamage amount


            val nearbyEntities = world.getNearbyEntities(
                LivingEntity::class.java,
                TargetingConditions.DEFAULT,
                player.serverPlayer!!,
                player.serverPlayer!!.boundingBox.inflate(range, range/2, range)
            )

            nearbyEntities.forEach { nearbyEntity ->
                nearbyEntity.hurt(nearbyEntity.damageSources().source(DamageTypes.FALL, player.serverPlayer!!), amount/2)
            }

            world.playSound(null, player.serverPlayer!!.posUnder, SoundEvents.ANVIL_FALL, SoundSource.BLOCKS, 2f, 1f)

            return@onTakeDamage 2f
        }
    }
}