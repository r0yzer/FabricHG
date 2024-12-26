package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import kotlin.random.Random

val magmaKit = kit("Magma") {
    kitSelectorItem = ItemStack(Items.MAGMA_BLOCK)

    usableInInvincibility = false
    description = "Burn your enemies when hitting them"

    val maxInt by property(4, "max int (0 = always fire)")
    val fireSeconds by property(2, "seconds on fire")

    kitEvents {
        onHitEntity { _, _, entity ->
            if ((entity as? ServerPlayer)?.hgPlayer?.isNeo == true) return@onHitEntity
            if (Random.nextInt(maxInt) == 0)
                entity.remainingFireTicks = fireSeconds * 20
        }
    }
}