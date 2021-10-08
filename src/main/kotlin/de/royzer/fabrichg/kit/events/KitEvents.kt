package de.royzer.fabrichg.kit.events

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.Kit
import net.minecraft.entity.Entity
import net.minecraft.server.network.ServerPlayerEntity


class KitEvents(
    var hitPlayerAction: ((HGPlayer, Kit, ServerPlayerEntity) -> Unit)? = null,
    var hitEntityAction: ((HGPlayer, Kit, Entity) -> Unit)? = null,
    var moveAction: ((HGPlayer, Kit) -> Unit)? = null,
)

fun onAttackEntity(target: Entity, serverPlayerEntity: ServerPlayerEntity) {
    val hgPlayer = serverPlayerEntity.hgPlayer
    hgPlayer.kits.forEach { kit ->
        if (hgPlayer.canUseKit(kit)) {
            kit.events.hitEntityAction?.invoke(hgPlayer, kit, target)
            if (target is ServerPlayerEntity) {
                kit.events.hitPlayerAction?.invoke(hgPlayer, kit, target)
            }
        }
    }
}

fun onMove(serverPlayerEntity: ServerPlayerEntity) {
    val hgPlayer = serverPlayerEntity.hgPlayer
    hgPlayer.kits.forEach { kit ->
        if (hgPlayer.canUseKit(kit)) {
            kit.events.moveAction?.invoke(hgPlayer, kit)
        }
    }
}