package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.kit
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import kotlin.random.Random

val magmaKit = kit("Magma") {
    kitSelectorItem = ItemStack(Items.MAGMA_BLOCK)

    usableInInvincibility = false
    description = "Burn your enemies when hitting them"

    kitEvents {
        onHitEntity { _, _, entity ->
            if ((entity as? ServerPlayer)?.hgPlayer?.isNeo == true) return@onHitEntity
            if (Random.nextInt(4) == 3)
                entity.setSecondsOnFire(2)
        }
    }
}