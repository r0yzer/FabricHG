package de.royzer.fabrichg.kit.events.kit.invoker

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.events.kit.invokeKitAction
import de.royzer.fabrichg.kit.kits.pacifistKit
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource

fun onTakeDamage(player: ServerPlayer, source: DamageSource, amount: Float): Float {
    val hgPlayer = player.hgPlayer
    var newDamage = amount

    hgPlayer.allKits.forEach { kit ->
        hgPlayer.invokeKitAction(kit, sendCooldown = false) {
            kit.events.takeDamageAction?.let { newDamage = it.invoke(hgPlayer, kit, source, newDamage) }
        }
    }

    if (hgPlayer.canUseKit(pacifistKit) || source.entity?.hgPlayer?.canUseKit(pacifistKit) == true) {
        newDamage = (amount * 0.75).toFloat()
    }

    return newDamage
}