package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.KitItem
import de.royzer.fabrichg.kit.cooldown.startCooldown
import de.royzer.fabrichg.kit.kit
import net.axay.fabrik.core.task.coroutineTask
import net.axay.fabrik.core.text.sendText
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity

val rougeKit = kit("Rouge") {
    addKitItem(
        KitItem(ItemStack(Items.GRAY_DYE)) { hgPlayer, kit ->
            val player = hgPlayer.serverPlayerEntity ?: return@KitItem
            val nearbyPlayers = player.world.getEntitiesByClass(ServerPlayerEntity::class.java, player.boundingBox.expand(8.0)) {
                it != player
            }
            nearbyPlayers.forEach { otherPlayer ->
                otherPlayer.hgPlayer.kits.forEach { kit ->
                    kit.onDisable?.invoke(otherPlayer.hgPlayer, kit)
                    otherPlayer.hgPlayer.kitsDisabled = true
                    coroutineTask(delay = 12000) {
                        otherPlayer.hgPlayer.kitsDisabled = false
                        kit.onEnable?.invoke(otherPlayer.hgPlayer, kit)
                    }
                }
            }
            hgPlayer.startCooldown(kit)
            hgPlayer.serverPlayerEntity!!.sendText {
                text("Du hast die Kits von ")
                text(nearbyPlayers.size.toString()) {
                    color = TEXT_BLUE
                }
                text(" Spielern disabled")
                color = TEXT_GRAY
            }
        }
    )
    kitSelectorItem = ItemStack(Items.GRAY_DYE)
    cooldown = 35.0
}