package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.Kit
import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import kotlin.random.Random

object MagmaKit : Kit() {
    override val name = "Magma"
    override val kitItem: ItemStack? = null
    override val kitSelectorItem = Items.MAGMA_BLOCK

    fun onAttackEntity(target: Entity, serverPlayerEntity: ServerPlayerEntity) {
        if (serverPlayerEntity.hgPlayer.hasKit(this))
            if (Random.nextInt(4) == 3)
                target.fireTicks = 40
    }
}