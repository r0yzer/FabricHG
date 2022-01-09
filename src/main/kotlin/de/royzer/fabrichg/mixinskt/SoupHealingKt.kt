package de.royzer.fabrichg.mixinskt

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import kotlin.math.min

object SoupHealingKt {
    fun onPotentialSoupUse (
        player: PlayerEntity, item: Item,
        cir: CallbackInfoReturnable<TypedActionResult<ItemStack>>,
        world: World, hand: Hand
    ) {
        val hungerManager = player.hungerManager

        if (!item.isStew || player.health >= player.maxHealth && !hungerManager.isNotFull) return

        var consumedSoup = false

        if (player.health < player.maxHealth) {
            player.heal(7F)
            consumedSoup = true
        } else if (hungerManager.isNotFull) {
            hungerManager.add(item.restoredFood, item.restoredSaturation)
            consumedSoup = true
        }

        if (consumedSoup) cir.returnValue = TypedActionResult.pass(ItemStack(Items.BOWL))
    }

    private val Item.isStew: Boolean
        get() = when (this) {
            Items.MUSHROOM_STEW -> true
            Items.BEETROOT_SOUP -> true
            Items.RABBIT_STEW -> true
            else -> false
        }

    private val Item.restoredFood: Int
        get() = this.foodComponent?.hunger ?: 0

    private val Item.restoredSaturation: Float
        get() = this.foodComponent?.saturationModifier ?: 0f
}
