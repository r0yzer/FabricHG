package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import net.minecraft.server.level.ServerPlayer
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
    val damageDivisor by property(2f, "damage divisor (falldamage)")
    val crouchDamage by property(1f, "crouch damage")
    
    val ignoreNeos by property(false, "ignore neos")

    kitEvents {
        onTakeDamage { hgPlayer, kit, source, amount ->
            if (!source.`is`(DamageTypes.FALL)) return@onTakeDamage amount

            val serverPlayer = hgPlayer.serverPlayer ?: return@onTakeDamage amount

            val world = serverPlayer.world

            val nearbyEntities = world.getNearbyEntities(
                LivingEntity::class.java,
                TargetingConditions.DEFAULT,
                serverPlayer,
                serverPlayer.boundingBox.inflate(range, range/2, range)
            ).filter {
                (it != serverPlayer) || (it.hgPlayer?.isNeo == false || ignoreNeos)
            }

            nearbyEntities.forEach { nearbyEntity ->
                if (nearbyEntity.isCrouching) {
                    nearbyEntity.hurt(serverPlayer.damageSources().playerAttack(serverPlayer), crouchDamage)
                } else {
                    nearbyEntity.hurt(serverPlayer.damageSources().playerAttack(serverPlayer), amount/damageDivisor)
                }
            }

            world.playSound(null, serverPlayer.posUnder, SoundEvents.ANVIL_FALL, SoundSource.BLOCKS, 2f, 1f)

            return@onTakeDamage 2f
        }
    }
}