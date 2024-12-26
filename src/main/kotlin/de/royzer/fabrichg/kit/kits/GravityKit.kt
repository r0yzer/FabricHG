package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.cooldown.checkUsesForCooldown
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.item.Items

val gravityKit = kit("Gravity") {
    cooldown = 40.0

    val maxUsesHit by property(3, "max uses (hit)")
    val maxUsesClick by property(1, "max uses (click)")

    val hitDuration by property(75, "levitation duration (hit)")
    val clickDuration by property(300, "levitation duration (click)")

    kitSelectorItem = Items.PURPLE_GLAZED_TERRACOTTA.defaultInstance

    description = "Send yourself or others in the sky"

    kitItem {
        itemStack = kitSelectorItem
        onClick { hgPlayer, _ ->
            hgPlayer.serverPlayer?.addEffect(MobEffectInstance(MobEffects.LEVITATION, clickDuration, 1, false, false))

            hgPlayer.checkUsesForCooldown(kit, maxUsesClick)
        }

        onHitPlayer { hgPlayer, kit, clickedPlayer ->
            if (clickedPlayer.hgPlayer.isNeo) return@onHitPlayer
            clickedPlayer.addEffect(MobEffectInstance(MobEffects.LEVITATION, hitDuration, 0, false, false))

            hgPlayer.checkUsesForCooldown(kit, maxUsesHit)
        }
    }
}