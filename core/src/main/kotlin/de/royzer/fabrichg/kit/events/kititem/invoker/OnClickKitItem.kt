package de.royzer.fabrichg.kit.events.kititem.invoker

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.events.kititem.isKitItem
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@JvmOverloads
fun onClick(
    player: Player,
    itemStack: ItemStack,
    cir: CallbackInfoReturnable<InteractionResultHolder<ItemStack>>? = null
) {
    val serverPlayerEntity = player as? ServerPlayer ?: return
    val hgPlayer = serverPlayerEntity.hgPlayer
    if (itemStack.isKitItem)
        hgPlayer.kits.forEach { kit ->
            kit.kitItems.forEach {
                if (it.itemStack.item == itemStack.item) {
                    it.invokeKitItemAction(hgPlayer, kit) {
                        it.clickAction?.invoke(hgPlayer, kit)
                    }
                }
            }
        }
}