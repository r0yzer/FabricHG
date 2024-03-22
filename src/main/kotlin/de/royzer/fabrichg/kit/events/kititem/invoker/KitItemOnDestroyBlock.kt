package de.royzer.fabrichg.kit.events.kititem.invoker

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.events.kititem.isKitItem
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer

fun onDestroyBlock(serverPlayer: ServerPlayer, pos: BlockPos) {
    val hgPlayer = serverPlayer.hgPlayer
    val itemStack = serverPlayer.mainHandItem
    hgPlayer.kits.forEach { kit ->
        kit.kitItems.forEach { kitItem ->
            if (itemStack.isKitItem) {
                kitItem.invokeKitItemAction(hgPlayer, kit) {
                    kitItem.destroyBlockAction?.invoke(hgPlayer, kit, pos)
                }
//                kitItem.invokeDestroyBlockAction(hgPlayer, kit, pos)
            }
        }
    }
}

//private fun KitItem.invokeDestroyBlockAction(
//    hgPlayer: HGPlayer,
//    kit: Kit,
//    pos: BlockPos,
//    ignoreCooldown: Boolean = false
//) {
//    if (hgPlayer.canUseKit(kit, ignoreCooldown)) {
//    } else if (hgPlayer.hasCooldown(kit)) {
//        hgPlayer.serverPlayer?.sendCooldown(kit)
//    }
//}