package de.royzer.fabrichg.kit.events.kit

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.cooldown.hasCooldown
import de.royzer.fabrichg.kit.cooldown.sendCooldown
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity

fun onRightClickEntity(serverPlayer: ServerPlayer, entity: Entity) {
    val hgPlayer = serverPlayer.hgPlayer
    hgPlayer.kits.forEach { kit ->
        if (hgPlayer.canUseKit(kit)) {
            kit.events.rightClickEntityAction?.invoke(hgPlayer, kit, entity)
        } else if (hgPlayer.hasCooldown(kit)) {
            serverPlayer.sendCooldown(kit)
        }
    }
}