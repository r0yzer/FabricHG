package de.royzer.fabrichg.kit.events.kititem.invoker

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.events.kititem.isKitItem
import de.royzer.fabrichg.kit.events.kititem.isKitItemOf
import de.royzer.fabrichg.sendPlayerStatus
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.context.BlockPlaceContext
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

fun onPlace(context: BlockPlaceContext, cir: CallbackInfoReturnable<InteractionResult>) {
    val serverPlayerEntity = (context.player as? ServerPlayer) ?: return
    cir.returnValue = InteractionResult.PASS
    serverPlayerEntity.hgPlayer.kits.forEach { kit ->
        if (context.itemInHand.isKitItemOf(kit)) {
            kit.kitItems.filter { it.itemStack.item == context.itemInHand.item }.forEach {
                val hgPlayer = serverPlayerEntity.hgPlayer
                it.invokeKitItemAction(hgPlayer, kit) {
                    it.clickAction?.invoke(hgPlayer, kit)
                }
                it.invokeKitItemAction(hgPlayer, kit) {
                    it.placeAction?.invoke(hgPlayer, kit, context.itemInHand, context.clickedPos, context.level)
                }
            }
        }
    }

    serverPlayerEntity.sendPlayerStatus()
}