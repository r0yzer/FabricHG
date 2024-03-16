package de.royzer.fabrichg.kit.events.kit

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.kit.Kit
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack


class KitEvents(
    var hitPlayerAction: ((HGPlayer, Kit, ServerPlayer) -> Unit)? = null,
    var hitEntityAction: ((HGPlayer, Kit, Entity) -> Unit)? = null,
    var moveAction: ((HGPlayer, Kit) -> Unit)? = null,
    var rightClickEntityAction: ((HGPlayer, Kit, clickedEntity: Entity) -> Unit)? = null,
    var drinkAction: ((HGPlayer, ItemStack) -> Unit)? = null,
)