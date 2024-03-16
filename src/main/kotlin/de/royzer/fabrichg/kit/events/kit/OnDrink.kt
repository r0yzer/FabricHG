package de.royzer.fabrichg.kit.events.kit

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.isKitItem
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.alchemy.PotionUtils

fun onDrink(itemStack: ItemStack, entity: LivingEntity) {
    if (entity !is ServerPlayer) return
    val hgPlayer = entity.hgPlayer
    hgPlayer.kits.forEach { kit ->
        kit.kitItems.forEach { kitItem ->
            if (itemStack.isKitItem && PotionUtils.getPotion(kitItem.itemStack) == PotionUtils.getPotion(itemStack)) {
                kitItem.invokeDrinkAction(hgPlayer, kit, itemStack)
            }
        }
        if (hgPlayer.canUseKit(kit)) {
            kit.events.drinkAction?.invoke(hgPlayer, itemStack)
        }
    }
}