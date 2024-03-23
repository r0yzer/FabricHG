package de.royzer.fabrichg.kit.events.kit.invoker

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.events.kit.invokeKitAction
import net.minecraft.server.level.ServerPlayer

fun onTick(serverPlayer: ServerPlayer) {
    val hgPlayer = serverPlayer.hgPlayer

    hgPlayer.kits.forEach {
        hgPlayer.invokeKitAction(it, sendCooldown = false, ignoreCooldown = true) {
            it.events.tickAction?.invoke(hgPlayer, it)
        }
    }
}