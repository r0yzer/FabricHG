package de.royzer.fabrichg.kit.events.kit.invoker

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.events.kit.invokeKitAction
import net.minecraft.world.entity.Pose
import net.minecraft.world.entity.player.Player

fun onSneak(player: Player, pose: Pose) {
    val hgPlayer = player.hgPlayer ?: return

    hgPlayer.kits.forEach { kit ->
        val ignoreCooldown = kit.events.noCooldownActions.contains<Any?>(kit.events.sneakAction)

        hgPlayer.invokeKitAction(kit, ignoreCooldown = ignoreCooldown) {
            kit.events.sneakAction?.invoke(hgPlayer, kit)
        }
    }
}