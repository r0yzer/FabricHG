package de.royzer.fabrichg.kit.events.kititem.invoker

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.events.kititem.isKitItem
import de.royzer.fabrichg.kit.events.kititem.isKitItemOf
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
    hgPlayer.kits.forEach { kit ->
        if (itemStack.isKitItemOf(kit)) {
            kit.kitItems.forEach {
                if (it.itemStack.item == itemStack.item) {
                    it.invokeKitItemAction(hgPlayer, kit, sendCooldown = it.clickAction != null) {
                        it.clickAction?.invoke(hgPlayer, kit)
                    }
                }
            }
        }
    }
}