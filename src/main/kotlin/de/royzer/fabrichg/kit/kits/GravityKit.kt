package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.cooldown.activateCooldown
import de.royzer.fabrichg.kit.cooldown.checkUsesForCooldown
import de.royzer.fabrichg.kit.kit
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.item.Items

val gravityKit = kit("Gravity") {
    cooldown = 40.0
    val maxUses = 1

    kitSelectorItem = Items.PHANTOM_MEMBRANE.defaultInstance

    kitItem {
        itemStack = kitSelectorItem
        onClick { hgPlayer, _ ->
            hgPlayer.serverPlayer?.addEffect(MobEffectInstance(MobEffects.LEVITATION, 300, 0, false, false))

            hgPlayer.activateCooldown(kit)
        }

        onClickAtPlayer { hgPlayer, _, clickedPlayer, _ ->
            clickedPlayer.addEffect(MobEffectInstance(MobEffects.LEVITATION, 75, 0, false, false))

            hgPlayer.activateCooldown(kit)
        }
    }
}