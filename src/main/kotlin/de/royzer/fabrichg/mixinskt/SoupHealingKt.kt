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
    fun onSoupUse(player: PlayerEntity, stack: ItemStack, cir: CallbackInfoReturnable<TypedActionResult<ItemStack>>, world: World, hand: Hand) {
//            if (player.hungerManager.isNotFull) player.hungerManager.add(6, 1F)
        if (player.health >= player.maxHealth) return
        player.heal(7F)
        player.clearActiveItem()
        cir.returnValue = TypedActionResult.pass(ItemStack(Items.BOWL))  //itemStack.use(world, player, hand)
    }
}