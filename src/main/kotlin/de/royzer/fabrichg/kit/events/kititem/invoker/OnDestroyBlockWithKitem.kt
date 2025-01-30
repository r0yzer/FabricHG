package de.royzer.fabrichg.kit.events.kititem.invoker

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.events.kit.invokeKitAction
import de.royzer.fabrichg.kit.events.kititem.isKitItem
import de.royzer.fabrichg.kit.events.kititem.isKitItemOf
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer

fun onDestroyBlock(serverPlayer: ServerPlayer, pos: BlockPos) {
    val hgPlayer = serverPlayer.hgPlayer
    val itemStack = serverPlayer.mainHandItem
    hgPlayer.allKits.forEach { kit ->
        hgPlayer.invokeKitAction(kit, sendCooldown = kit.events.destroyBlockAction != null) {
            kit.events.destroyBlockAction?.invoke(hgPlayer, kit, pos)
        }
        kit.kitItems.forEach { kitItem ->
            if (itemStack.isKitItemOf(kit)) {
                kitItem.invokeKitItemAction(hgPlayer, kit, sendCooldown = kitItem.destroyBlockAction != null) {
                    kitItem.destroyBlockAction?.invoke(hgPlayer, kit, pos)
                }
            }
        }
    }
}