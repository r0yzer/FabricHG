package de.royzer.fabrichg.kit.events.kititem

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.isKitItem
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer

fun onDestroyBlock(serverPlayer: ServerPlayer, pos: BlockPos) {
    val hgPlayer = serverPlayer.hgPlayer
    val itemStack = serverPlayer.mainHandItem
    hgPlayer.kits.forEach { kit ->
        kit.kitItems.forEach { kitItem ->
            if (itemStack.isKitItem) {
                kitItem.invokeDestroyBlockAction(hgPlayer, kit, pos)
            }
        }
    }
}