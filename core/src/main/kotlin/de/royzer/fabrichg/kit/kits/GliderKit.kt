package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.kit
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.item.Items

val gliderKit = kit("Glider") {

    kitSelectorItem = Items.FEATHER.defaultInstance

    description = "Glide safely"

    kitItem {
        itemStack = kitSelectorItem
        whenHeld { hgPlayer, _ ->
            hgPlayer.serverPlayer?.addEffect(MobEffectInstance(MobEffects.SLOW_FALLING, 2, 2, false, false, false))
        }
    }
}