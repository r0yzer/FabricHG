package de.royzer.fabrichg.kit

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level

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

    fun onPlace(action: (HGPlayer, Kit, ItemStack, BlockPos, Level) -> Unit) {
        kitItem.placeAction = action
    }

    fun onClickAtEntity(action: (HGPlayer, Kit, Entity, InteractionHand) -> Unit) {
        kitItem.clickAtEntityAction = action
    }

    fun onClickAtPlayer(action: (hgPlayer: HGPlayer, kit: Kit, clickedPlayer: ServerPlayer, hand: InteractionHand) -> Unit) {
        kitItem.clickAtPlayerAction = action
    }

    fun onUseBlock(action: ((HGPlayer, Kit, BlockPlaceContext) -> Unit)?){
        kitItem.useOnBlockAction = action
    }

    fun onHitPlayer(action: ((HGPlayer, Kit, ServerPlayer) -> Unit)?) {
        kitItem.hitPlayerAction = action
    }

    fun onHitEntity(action: ((HGPlayer, Kit, Entity) -> Unit)?) {
        kitItem.hitEntityAction = action
    }

}