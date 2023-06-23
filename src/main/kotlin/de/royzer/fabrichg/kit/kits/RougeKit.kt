package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.cooldown.activateCooldown
import de.royzer.fabrichg.kit.kit
import net.silkmc.silk.core.task.coroutineTask
import net.silkmc.silk.core.text.sendText
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

val rougeKit = kit("Rouge") {

    kitSelectorItem = ItemStack(Items.GRAY_DYE)
    cooldown = 35.0

    kitItem {
        onClick { hgPlayer, kit ->
            val player = hgPlayer.serverPlayerEntity ?: return@onClick
            val nearbyPlayers = player.level().getEntitiesOfClass(ServerPlayer::class.java, player.boundingBox.expandTowards(8.0, 8.0, 8.0)) {
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
            hgPlayer.activateCooldown(kit)
            hgPlayer.serverPlayerEntity!!.sendText {
                text("Du hast die Kits von ")
                text(nearbyPlayers.size.toString()) {
                    color = TEXT_BLUE
                }
                text(" Spielern disabled")
                color = TEXT_GRAY
            }
        }
    }

}