package de.royzer.fabrichg.kit.events.kit.invoker

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.events.kit.invokeKitAction
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand

fun onLeftClick(serverPlayer: ServerPlayer, hand: InteractionHand) {
    val hgPlayer = serverPlayer.hgPlayer
    hgPlayer.kits.forEach { kit ->
        hgPlayer.invokeKitAction(kit, sendCooldown = false) {
            kit.events.leftClickAction?.invoke(hgPlayer, kit)
        }
    }
}