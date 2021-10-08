package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.kit
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import kotlin.random.Random

val magmaKit = kit("Magma") {
    kitSelectorItem = ItemStack(Items.MAGMA_BLOCK)

    useableInInvincibility = false

    events {
        onHitEntity { _, _, entity ->
            if (Random.nextInt(4) == 3)
                entity.fireTicks += 40
        }
    }
}