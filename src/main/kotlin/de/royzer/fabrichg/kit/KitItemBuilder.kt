package de.royzer.fabrichg.kit

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class KitItemBuilder(val kitItem: KitItem) {
    var itemStack: ItemStack = kitItem.itemStack
        set(value) {
            kitItem.itemStack = value
            field = value
        }

    var droppable: Boolean = kitItem.droppable
        set(value) {
            kitItem.droppable = value
            field = value
        }

    fun onClick(action: (HGPlayer, Kit) -> Unit) {
        kitItem.clickAction = action
    }

    fun onPlace(action: (HGPlayer, Kit, ItemStack, BlockPos, World) -> Unit) {
        kitItem.placeAction = action
    }

    fun onClickAtEntity(action: (HGPlayer, Kit, Entity, Hand) -> Unit) {
        kitItem.clickAtEntityAction = action
    }

    fun onClickAtPlayer(action: (hgPlayer: HGPlayer, kit: Kit, clickedPlayer: ServerPlayerEntity, hand: Hand) -> Unit) {
        kitItem.clickAtPlayerAction = action
    }

}