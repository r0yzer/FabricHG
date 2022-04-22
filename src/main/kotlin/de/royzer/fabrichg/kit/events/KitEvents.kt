package de.royzer.fabrichg.kit.events

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.Kit
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity


class KitEvents(
    var hitPlayerAction: ((HGPlayer, Kit, ServerPlayer) -> Unit)? = null,
    var hitEntityAction: ((HGPlayer, Kit, Entity) -> Unit)? = null,
    var moveAction: ((HGPlayer, Kit) -> Unit)? = null,
)

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

fun onMove(serverPlayerEntity: ServerPlayer) {
    val hgPlayer = serverPlayerEntity.hgPlayer
    hgPlayer.kits.forEach { kit ->
        if (hgPlayer.canUseKit(kit)) {
            kit.events.moveAction?.invoke(hgPlayer, kit)
        }
    }
}