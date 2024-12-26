package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.item.Items
import kotlin.random.Random

val snailKit = kit("Snail") {
    kitSelectorItem = Items.SOUL_SAND.defaultInstance

    usableInInvincibility = false
    description = "Give your enemies slowness when attacking them"

    val maxInt by property(4, "max int (0 = always slow)")

    kitEvents {
        onHitPlayer { _, _, target ->
            if (!target.hgPlayer.isNeo && Random.nextInt(maxInt) == 0)
                target.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 25, 1))
        }
    }
}