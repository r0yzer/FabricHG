package de.royzer.fabrichg.kit.events.kit.invoker

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.cooldown.sendCooldown
import net.minecraft.world.entity.Pose
import net.minecraft.world.entity.player.Player

fun onSneak(player: Player, pose: Pose) {
    val hgPlayer = player.hgPlayer ?: return

    hgPlayer.kits.forEach {
        if (hgPlayer.canUseKit(it)) {
            it.events.sneakAction?.invoke(hgPlayer, it)
        } else {
            hgPlayer.sendCooldown(it)
        }
    }
}