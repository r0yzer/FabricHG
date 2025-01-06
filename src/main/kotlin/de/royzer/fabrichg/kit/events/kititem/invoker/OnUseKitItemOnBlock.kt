package de.royzer.fabrichg.kit.events.kititem.invoker

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.events.kititem.isKitItem
import de.royzer.fabrichg.kit.events.kititem.isKitItemOf
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.UseOnContext

fun onUseOnBlock(player: Player, context: UseOnContext) {
    val serverPlayerEntity = player as? ServerPlayer ?: return
    val hgPlayer = serverPlayerEntity.hgPlayer
    hgPlayer.kits.forEach { kit ->
        if (context.itemInHand.isKitItemOf(kit)) {
            kit.kitItems.forEach { kitItem ->
                if (kitItem.itemStack.item == context.itemInHand.item) {
                    kitItem.invokeKitItemAction(hgPlayer, kit) {
                        kitItem.useOnBlockAction?.invoke(hgPlayer, kit, context)
                    }
                }
            }
        }
    }
}