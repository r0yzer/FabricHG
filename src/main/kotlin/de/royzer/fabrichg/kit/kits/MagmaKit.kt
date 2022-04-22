package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.kit
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import kotlin.random.Random

val magmaKit = kit("Magma") {
    kitSelectorItem = ItemStack(Items.MAGMA_BLOCK)

    usableInInvincibility = false

    events {
        onHitEntity { _, _, entity ->
            if (Random.nextInt(4) == 3)
                entity.setSecondsOnFire(2)
        }
    }
}