package de.royzer.fabrichg.kit.events.kit

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity

fun onAttackEntity(target: Entity, serverPlayerEntity: ServerPlayer) {
    val hgPlayer = serverPlayerEntity.hgPlayer
    hgPlayer.kits.forEach { kit ->
        if (hgPlayer.canUseKit(kit)) {
            kit.events.hitEntityAction?.invoke(hgPlayer, kit, target)
            if (target is ServerPlayer) {
                kit.events.hitPlayerAction?.invoke(hgPlayer, kit, target)
            }
        }
    }
}