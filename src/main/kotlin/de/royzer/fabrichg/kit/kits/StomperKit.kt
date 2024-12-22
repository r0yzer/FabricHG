package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.achievements.delegate.achievement
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Items
import net.silkmc.silk.core.entity.posUnder
import net.silkmc.silk.core.entity.world
import kotlin.math.roundToInt

val stomperKit = kit("Stomper") {
    kitSelectorItem = Items.DIAMOND_BOOTS.defaultInstance

    description = "stop players to deal damage"

    val range by property(7.5, "stomper range")
    val damageDivisor by property(2f, "damage divisor (falldamage)")
    val crouchDamage by property(1f, "crouch damage")
    
    val ignoreNeos by property(false, "ignore neos")

    val dealStomperDamageAchievement by achievement("stomper damage") {
        level(100)
        level(300)
        level(1000)
    }
    val stomperKillPlayersAchievement by achievement("kill players") {
        level(5)
        level(30)
        level(150)
    }

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
                (it != serverPlayer)
            }.filter {
                // was
                (it.hgPlayer?.isNeo == false || ignoreNeos)
            }

            nearbyEntities.forEach { nearbyEntity ->
                if (nearbyEntity.isCrouching) {
                    nearbyEntity.hurt(serverPlayer.damageSources().playerAttack(serverPlayer), crouchDamage)
                    dealStomperDamageAchievement.awardLater(serverPlayer, crouchDamage.roundToInt())
                } else {
                    nearbyEntity.hurt(serverPlayer.damageSources().playerAttack(serverPlayer), amount/damageDivisor)
                    dealStomperDamageAchievement.awardLater(serverPlayer, (amount/damageDivisor).roundToInt())
                }

                if (nearbyEntity is Player && nearbyEntity.isDeadOrDying) {
                    stomperKillPlayersAchievement.awardLater(serverPlayer)
                }
            }

            world.playSound(null, serverPlayer.posUnder, SoundEvents.ANVIL_FALL, SoundSource.BLOCKS, 2f, 1f)

            return@onTakeDamage 2f
        }
    }
}