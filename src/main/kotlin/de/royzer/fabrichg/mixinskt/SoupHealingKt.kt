package de.royzer.fabrichg.mixinskt

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

const val SOUP_HEAL = 7F

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
            player.hgPlayer?.kits?.forEach {
                it.events.soupEatAction?.invoke(player.hgPlayer!!) // only on heal soups not hunger soups
            }
            player.heal(SOUP_HEAL)
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
