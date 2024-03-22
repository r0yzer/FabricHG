package de.royzer.fabrichg.kit.events.kit

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.kit.Kit
import de.royzer.fabrichg.kit.cooldown.hasCooldown
import de.royzer.fabrichg.kit.cooldown.sendCooldown
import de.royzer.fabrichg.kit.events.kititem.KitItem
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack


class KitEvents(
    var hitPlayerAction: ((HGPlayer, Kit, ServerPlayer) -> Unit)? = null,
    var hitEntityAction: ((HGPlayer, Kit, Entity) -> Unit)? = null,
    var moveAction: ((HGPlayer, Kit) -> Unit)? = null,
    var rightClickEntityAction: ((HGPlayer, Kit, clickedEntity: Entity) -> Unit)? = null,
    var drinkAction: ((HGPlayer, ItemStack) -> Unit)? = null,
    var soupEatAction: ((HGPlayer) -> Unit)? = null,
    var killPlayerAction: ((HGPlayer, ServerPlayer) -> Unit)? = null,
    var sneakAction: ((HGPlayer, Kit) -> Unit)? = null
)

fun HGPlayer.invokeKitAction(kit: Kit, sendCooldown: Boolean = true, ignoreCooldown: Boolean = false, action: () -> Unit) {
    if (this.canUseKit(kit, ignoreCooldown)) {
        action.invoke()
    } else if (this.hasCooldown(kit)) {
        if (sendCooldown) {
            this.serverPlayer?.sendCooldown(kit)
        }
    }
}