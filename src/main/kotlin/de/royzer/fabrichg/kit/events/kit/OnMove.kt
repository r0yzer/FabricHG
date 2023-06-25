package de.royzer.fabrichg.kit.events.kit

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import net.minecraft.server.level.ServerPlayer


fun onMove(serverPlayerEntity: ServerPlayer) {
    val hgPlayer = serverPlayerEntity.hgPlayer
    hgPlayer.kits.forEach { kit ->
        if (hgPlayer.canUseKit(kit)) {
            kit.events.moveAction?.invoke(hgPlayer, kit)
        }
    }
}