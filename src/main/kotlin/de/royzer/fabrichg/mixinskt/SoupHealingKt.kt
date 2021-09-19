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
        if (player.health >= player.maxHealth) {
            if (player.hungerManager.isNotFull) player.hungerManager.add(6, 1F)
            cir.returnValue = TypedActionResult.pass(ItemStack(Items.BOWL))
            return
        }
        player.heal(7F)
        cir.returnValue = TypedActionResult.pass(ItemStack(Items.BOWL))
    }
}