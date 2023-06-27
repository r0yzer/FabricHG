package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.kit
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.item.Items

val cannibalKit = kit("Cannibal") {
    kitSelectorItem = Items.COD.defaultInstance

    usableInInvincibility = false

    kitEvents {
        onHitPlayer { _, _, target ->
            if (!target.hgPlayer.isNeo)
                target.addEffect(MobEffectInstance(MobEffects.HUNGER, 30, 1))
        }
    }
}