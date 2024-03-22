package de.royzer.fabrichg.kit.events.kititem.invoker

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.events.kit.invoker.onRightClickEntity
import de.royzer.fabrichg.kit.events.kititem.isKitItem
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

fun onClickAtEntity(
    clickingPlayer: Player,
    hand: InteractionHand,
    clickedEntity: Entity,
    cir: CallbackInfoReturnable<InteractionResult>
) {
    val serverPlayer = clickingPlayer as? ServerPlayer
    val hgBot = clickingPlayer as? ServerPlayer
    val hgPlayer = serverPlayer?.hgPlayer ?: hgBot?.hgPlayer ?: return
    val mainHandStack = serverPlayer?.mainHandItem ?: hgBot?.mainHandItem ?: return
    val offhandStack = serverPlayer?.offhandItem ?: hgBot?.offhandItem ?: return
    if (mainHandStack.isKitItem || offhandStack.isKitItem) {
        hgPlayer.kits.forEach { kit ->
            kit.kitItems.forEach { kitItem ->
                if (kitItem.itemStack.item == mainHandStack.item || offhandStack.item == kitItem.itemStack.item) {
                    kitItem.invokeKitItemAction(hgPlayer, kit) {
                        if (clickedEntity is ServerPlayer) {
                            kitItem.clickAtPlayerAction?.invoke(hgPlayer, kit, clickedEntity, hand)
                        }
                        else {
                            kitItem.clickAtEntityAction?.invoke(hgPlayer, kit, clickedEntity, hand)
                        }
                    }
//                    kitItem.invokeClickAtEntityAction(hgPlayer, kit, clickedEntity, hand)
                }
            }
        }
    } else {
        hgPlayer.kits.forEach { kit ->
            if (serverPlayer != null) {
                onRightClickEntity(serverPlayer.hgPlayer, clickedEntity)
            }
        }
    }
}