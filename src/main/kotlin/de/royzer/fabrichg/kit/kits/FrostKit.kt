package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.kit
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.FrostWalkerEnchantment
import net.silkmc.silk.core.entity.posUnder

val frostKit = kit("frost") {
    kitSelectorItem = Items.PACKED_ICE.defaultInstance
    description = "Your feet are freezing"

    kitEvents {
        onMove { hgPlayer, _ ->
            hgPlayer.serverPlayer?.let {
                FrostWalkerEnchantment.onEntityMoved(it, it.level(), it.posUnder, 3)
            }
        }
    }
}