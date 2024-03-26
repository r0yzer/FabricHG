package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.item.Items

val cannibalKit = kit("Cannibal") {
    kitSelectorItem = Items.COD.defaultInstance

    usableInInvincibility = false
    description = "Give your enemies hunger when attacking them"

    val hungerDuration by property(30, "hunger duration")
    val hungerAmplifier by property(1, "hunger amplifier")

    kitEvents {
        onHitPlayer { _, _, target ->
            if (!target.hgPlayer.isNeo)
                target.addEffect(MobEffectInstance(MobEffects.HUNGER, hungerDuration, hungerAmplifier))
        }
    }
}