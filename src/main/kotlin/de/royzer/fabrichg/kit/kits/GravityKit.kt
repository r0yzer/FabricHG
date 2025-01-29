package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.cooldown.checkUsesForCooldown
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import kotlinx.coroutines.cancel
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.Items
import net.silkmc.silk.core.kotlin.ticks
import net.silkmc.silk.core.task.mcCoroutineTask

const val FLYING_KEY = "gravityFlying"

val gravityKit = kit("Gravity") {
    cooldown = 20.0

    maxUses = 3

    val hitDuration by property(75, "levitation duration (hit)")
    val clickDuration by property(200, "levitation duration (click)")

    kitSelectorItem = Items.PURPLE_GLAZED_TERRACOTTA.defaultInstance

    description = "Send yourself or others in the sky"

    kitItem {
        itemStack = kitSelectorItem
        onClick { hgPlayer, _ ->
            val serverPlayer = hgPlayer.serverPlayer ?: return@onClick
            if (hgPlayer.getPlayerData<Boolean>(FLYING_KEY) == true) {
                serverPlayer.removeEffect(MobEffects.LEVITATION)
                hgPlayer.playerData[FLYING_KEY] = false
                hgPlayer.checkUsesForCooldown(kit, maxUses!!, 3)
            } else {
                serverPlayer.addEffect(MobEffectInstance(MobEffects.LEVITATION, clickDuration, 1, false, false))
                hgPlayer.playerData[FLYING_KEY] = true
                mcCoroutineTask(delay = clickDuration.ticks) {
                    // das ist bisschen verbuggt wenn man cooldown skip macht oder der cooldown geringer als die flugzeit ist aber das passiert ja normal nicht
                    // und wenn man während man selber 3x wen hitted während man fliegt kann man nicht mehr abbrechen
                    // das muss man aber erstmal machen
                    // alternative wäre halt gar nicht hitten aber das ist auch cöp
                    if (hgPlayer.getPlayerData<Boolean>(FLYING_KEY) == true) {
                        serverPlayer.removeEffect(MobEffects.LEVITATION)
                        hgPlayer.playerData[FLYING_KEY] = false
                        hgPlayer.checkUsesForCooldown(kit, maxUses!!, 3)
                        this.cancel("sammy wenn mikasa.mp3")
                    }
                }
            }
        }

        onHitEntity { hgPlayer, kit, entity ->
            if (entity.hgPlayer?.isNeo == true) return@onHitEntity
            (entity as? LivingEntity)?.addEffect(MobEffectInstance(MobEffects.LEVITATION, hitDuration, 0, false, false))

            hgPlayer.checkUsesForCooldown(kit, maxUses!!)
        }
    }
}