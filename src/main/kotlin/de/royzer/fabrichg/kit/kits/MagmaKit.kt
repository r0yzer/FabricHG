package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.kit
import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import kotlin.random.Random

val magmaKit = kit("Magma") {
    kitSelectorItem = ItemStack(Items.MAGMA_BLOCK)
}

fun onAttackEntity(target: Entity, serverPlayerEntity: ServerPlayerEntity) {
    if (serverPlayerEntity.hgPlayer.canUseKit(magmaKit, true))
        if (Random.nextInt(4) == 3)
            target.fireTicks += 40
}