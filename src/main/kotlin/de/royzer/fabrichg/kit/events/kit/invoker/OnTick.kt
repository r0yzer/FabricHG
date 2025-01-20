package de.royzer.fabrichg.kit.events.kit.invoker

import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.kit.events.kit.invokeKitAction
import de.royzer.fabrichg.kit.events.kititem.isKitItem
import net.minecraft.server.level.ServerPlayer

fun onTick(serverPlayer: ServerPlayer) {
    val hgPlayer = PlayerList.getPlayer(serverPlayer.uuid) ?: return

    hgPlayer.allKits.forEach { kit ->
        hgPlayer.invokeKitAction(kit, sendCooldown = false, ignoreCooldown = true) {
            kit.events.tickAction?.invoke(hgPlayer, kit)
        }
        kit.kitItems.forEach { kitItem ->
            val handItem = serverPlayer.mainHandItem
            if (kitItem.itemStack.item == handItem.item && handItem.isKitItem) { // junge wie compared man itemstacks
                kitItem.invokeKitItemAction(hgPlayer, kit, sendCooldown = false, ignoreCooldown = false) {
                    kitItem.whenHeldAction?.invoke(hgPlayer, kit)
                }
            }
        }

    }
}