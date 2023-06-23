package de.royzer.fabrichg.mixinskt

import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

object SoupHealingKt {
    fun onPotentialSoupUse (
        player: Player, item: Item,
        cir: CallbackInfoReturnable<InteractionResultHolder<ItemStack>>,
        world: Level, hand: InteractionHand
    ) {
        val foodData = player.foodData

        if (!item.isStew || player.health >= player.maxHealth && !foodData.needsFood()) return

        var consumedSoup = false

        if (player.health < player.maxHealth) {
            player.heal(7F)
            consumedSoup = true
        } else if (foodData.needsFood()) {
            foodData.foodLevel += item.restoredFood
            consumedSoup = true
        }

        if (consumedSoup) cir.returnValue = InteractionResultHolder.pass(ItemStack(Items.BOWL))
    }

    private val Item.isStew: Boolean
        get() = when (this) {
            Items.MUSHROOM_STEW -> true
            Items.BEETROOT_SOUP -> true
            Items.RABBIT_STEW -> true
            else -> false
        }

    private val Item.restoredFood: Int
        get() = this.foodProperties?.nutrition ?: 0

    private val Item.restoredSaturation: Float
        get() = this.foodProperties?.saturationModifier ?: 0f
}
