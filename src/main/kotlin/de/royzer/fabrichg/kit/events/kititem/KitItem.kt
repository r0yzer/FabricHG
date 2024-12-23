package de.royzer.fabrichg.kit.events.kititem

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.kit.Kit
import de.royzer.fabrichg.kit.cooldown.hasCooldown
import de.royzer.fabrichg.kit.cooldown.sendCooldown
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponents
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level

fun kitItem(itemStack: ItemStack): KitItem {
    return KitItem(itemStack)
}

class KitItem(
    var itemStack: ItemStack,
    var droppable: Boolean = false,
    internal var clickAtEntityAction: ((HGPlayer, Kit, Entity, InteractionHand) -> Unit)? = null,
    internal var clickAtPlayerAction: ((HGPlayer, Kit, ServerPlayer, InteractionHand) -> Unit)? = null,
    internal var placeAction: ((HGPlayer, Kit, ItemStack, BlockPos, Level) -> Unit)? = null,
    internal var clickAction: ((HGPlayer, Kit) -> Unit)? = null,
    internal var useOnBlockAction: ((HGPlayer, Kit, UseOnContext) -> Unit)? = null,
    internal var hitPlayerAction: ((HGPlayer, Kit, ServerPlayer) -> Unit)? = null,
    internal var hitEntityAction: ((HGPlayer, Kit, Entity) -> Unit)? = null,
    internal var drinkAction: ((HGPlayer, Kit, ItemStack) -> Unit)? = null,
    internal var destroyBlockAction: ((HGPlayer, Kit, BlockPos) -> Unit)? = null,
    internal var whenHeldAction: ((HGPlayer, Kit) -> Unit)? = null,
) {
    // prüft auf cooldown und ruft ggf. die übergebene action auf (meistens das invoken der entsprechenden action) + sendet ggf. cooldown msg
    fun invokeKitItemAction(hgPlayer: HGPlayer, kit: Kit, sendCooldown: Boolean = true, ignoreCooldown: Boolean = false, action: () -> Unit) {
        if (hgPlayer.canUseKit(kit, ignoreCooldown)) {
            action.invoke()
        } else if (hgPlayer.hasCooldown(kit)) {
            if (sendCooldown) {
                hgPlayer.serverPlayer?.sendCooldown(kit)
            }
        }
    }
}

// hehe
val ItemStack.isKitItem: Boolean
    get() {
        val lore = this.get(DataComponents.LORE).toString() // TODO
        return lore.contains("Kititem")
    }