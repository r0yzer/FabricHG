package de.royzer.fabrichg.kit.events.kit.invoker

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.events.kit.invokeKitAction
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource

fun onTakeDamage(player: ServerPlayer, source: DamageSource, amount: Float): Float {
    val hgPlayer = player.hgPlayer
    var newDamage = amount

    hgPlayer.kits.forEach { kit ->
        hgPlayer.invokeKitAction(kit, sendCooldown = false) {
            kit.events.takeDamageAction?.let { newDamage = it.invoke(hgPlayer, source, newDamage) }
        }
    }

    return newDamage
}