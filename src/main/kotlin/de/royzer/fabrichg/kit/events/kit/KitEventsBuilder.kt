package de.royzer.fabrichg.kit.events.kit

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.kit.Kit
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack

class KitEventsBuilder(val kit: Kit) {
    fun onHitPlayer(action: (HGPlayer, Kit, ServerPlayer) -> Unit) {
        kit.events.hitPlayerAction = action
    }
    fun onHitEntity(action: (HGPlayer, Kit, Entity) -> Unit) {
        kit.events.hitEntityAction = action
    }
    fun onMove(action: (HGPlayer, Kit) -> Unit) {
        kit.events.moveAction = action
    }
    fun onRightClickEntity(action: (HGPlayer, Kit, Entity) -> Unit) {
        kit.events.rightClickEntityAction = action
    }
    fun onDrink(action: (HGPlayer, ItemStack) -> Unit) {
        kit.events.drinkAction = action
    }
    fun onSoupEat(action: (HGPlayer) -> Unit) {
        kit.events.soupEatAction = action
    }
    fun onKillPlayer(action: (HGPlayer, ServerPlayer) -> Unit) {
        kit.events.killPlayerAction = action
    }
}