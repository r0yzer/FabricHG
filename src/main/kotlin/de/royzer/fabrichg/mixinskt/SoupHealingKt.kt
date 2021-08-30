package de.royzer.fabrichg.mixinskt

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

object SoupHealingKt {
    @JvmStatic
    fun onSoupUse(player: PlayerEntity, itemStack: ItemStack, cir: CallbackInfoReturnable<TypedActionResult<ItemStack>>, world: World, hand: Hand) {
        val stack = player.getStackInHand(hand)
        if (stack.item == Items.MUSHROOM_STEW) {
//            if (player.hungerManager.isNotFull) player.hungerManager.add(6, 1F)
            if (player.health >= player.maxHealth) return
            player.heal(7F)
//            with(player.inventory) {
//                setStack(selectedSlot, Items.BOWL.defaultStack)
//            }
            player.clearActiveItem()
            cir.returnValue = TypedActionResult.pass(ItemStack(Items.BOWL))  //itemStack.use(world, player, hand)
        }
    }
}