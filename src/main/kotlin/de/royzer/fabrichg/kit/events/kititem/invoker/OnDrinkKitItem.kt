package de.royzer.fabrichg.kit.events.kititem.invoker

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.events.kit.invokeKitAction
import de.royzer.fabrichg.kit.events.kititem.isKitItemOf
import de.royzer.fabrichg.kit.kits.beerPotion
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack

fun onDrink(itemStack: ItemStack, entity: LivingEntity) { // TODO
    val hgPlayer = entity.hgPlayer ?: return
    hgPlayer.allKits.forEach { kit ->
        kit.kitItems.forEach kitItemForEach@ { kitItem ->
            if (kitItem.itemStack.beerPotion == null) return@kitItemForEach

            if (itemStack.isKitItemOf(kit)) {
                kitItem.invokeKitItemAction(hgPlayer, kit, sendCooldown = kitItem.drinkAction != null) {
                    kitItem.drinkAction?.invoke(hgPlayer, kit, itemStack)
                }
            }
        }
        if (hgPlayer.canUseKit(kit)) {
            hgPlayer.invokeKitAction(kit, sendCooldown = kit.events.drinkAction != null) {
                kit.events.drinkAction?.invoke(hgPlayer, kit, itemStack)
            }
        }
    }
}