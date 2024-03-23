package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.cooldown.activateCooldown
import de.royzer.fabrichg.kit.events.kit.invoker.onTick
import de.royzer.fabrichg.kit.kit
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Items
import net.silkmc.silk.core.entity.modifyVelocity
import net.silkmc.silk.core.task.mcCoroutineTask
import net.silkmc.silk.core.text.sendText
import kotlin.time.Duration.Companion.milliseconds

private const val flyingKey = "phantomFlying"

val phantomKit = kit("Phantom") {
    kitSelectorItem = Items.PHANTOM_MEMBRANE.defaultInstance

    description = "Glide like a phantom"

    cooldown = 45.0


    kitItem {
        itemStack = kitSelectorItem

        onClick { hgPlayer, kit ->
            val inFight = hgPlayer.inFight
            hgPlayer.serverPlayer?.modifyVelocity(0, if (inFight) 0.75 else 2.5,0, true)

            if (inFight) {
                hgPlayer.serverPlayer?.sendText {
                    text("Your leap was weakened because you are in a fight")
                    color = TEXT_GRAY
                }
            }

            // delay because otherwise if the player is on ground sometimes it stops directly
            mcCoroutineTask(delay = 200.milliseconds) {
                hgPlayer.serverPlayer?.startFallFlying()
                hgPlayer.playerData[flyingKey] = true
            }

            hgPlayer.activateCooldown(kit)
        }
    }

    kitEvents {
        onTick { hgPlayer, kit ->
            val player = hgPlayer.serverPlayer ?: return@onTick
            if (player.onGround()) {
                player.hgPlayer.playerData[flyingKey] = false
            }
        }
    }

}

fun shouldGlide(player: ServerPlayer): Boolean {
    return player.hgPlayer.getPlayerData<Boolean>(flyingKey) == true
}