package de.royzer.fabrichg.kit.events.kititem.invoker

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.events.kititem.isKitItem
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.PotionBrewing
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.item.alchemy.Potions

fun onDrink(itemStack: ItemStack, entity: LivingEntity) { // TODO
//    val hgPlayer = entity.hgPlayer ?: return
//    hgPlayer.kits.forEach { kit ->
//        kit.kitItems.forEach { kitItem ->
//            PotionContents.createItemStack(Items.SPLASH_POTION, kitItem.itemStack.pot)
//            if (itemStack.isKitItem && PotionBrewing.getPotion(kitItem.itemStack) == PotionUtils.getPotion(itemStack)) {
//                kitItem.invokeKitItemAction(hgPlayer, kit) {
//                    kitItem.drinkAction?.invoke(hgPlayer, kit, itemStack)
//                }
//            }
//        }
//        if (hgPlayer.canUseKit(kit)) {
//            kit.events.drinkAction?.invoke(hgPlayer, kit, itemStack)
//        }
//    }
}