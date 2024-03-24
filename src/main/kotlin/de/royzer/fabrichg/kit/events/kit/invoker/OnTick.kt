package de.royzer.fabrichg.kit.events.kit.invoker

import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.kit.events.kit.invokeKitAction
import net.minecraft.server.level.ServerPlayer

fun onTick(serverPlayer: ServerPlayer) {
    val hgPlayer = PlayerList.getPlayer(serverPlayer.uuid) ?: return

    hgPlayer.kits.forEach {
        hgPlayer.invokeKitAction(it, sendCooldown = false, ignoreCooldown = true) {
            it.events.tickAction?.invoke(hgPlayer, it)
        }
    }
}