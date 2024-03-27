package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.cooldown.activateCooldown
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import net.silkmc.silk.core.task.coroutineTask
import net.silkmc.silk.core.text.sendText
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.silkmc.silk.core.task.mcCoroutineTask
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

val rougeKit = kit("Rouge") {
    kitSelectorItem = ItemStack(Items.GRAY_DYE)
    cooldown = 35.0
    description = "Disable the kits of your enemies"

    val range by property(8.0, "kit disable range")
    val disableTime by property(12, "kit disable time (seconds)")

    kitItem {
        itemStack = kitSelectorItem
        onClick { hgPlayer, kit ->
            val player = hgPlayer.serverPlayer ?: return@onClick
            val nearbyPlayers = player.level().getEntitiesOfClass(ServerPlayer::class.java, player.boundingBox.expandTowards(range, range, range)) {
                it != player || !it.hgPlayer.isNeo
            }
            nearbyPlayers.forEach { otherPlayer ->
                otherPlayer.hgPlayer.kits.forEach { kit ->
                    kit.onDisable?.invoke(otherPlayer.hgPlayer, kit)
                    otherPlayer.hgPlayer.kitsDisabled = true
                    mcCoroutineTask(delay = disableTime.seconds) {
                        otherPlayer.hgPlayer.kitsDisabled = false
                        kit.onEnable?.invoke(otherPlayer.hgPlayer, kit, otherPlayer)
                    }
                }
            }
            hgPlayer.activateCooldown(kit)
            hgPlayer.serverPlayer!!.sendText {
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