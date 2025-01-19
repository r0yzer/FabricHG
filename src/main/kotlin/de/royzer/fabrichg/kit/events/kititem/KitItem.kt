package de.royzer.fabrichg.kit.events.kititem

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.data.hgplayer.hasCustomHoverName
import de.royzer.fabrichg.kit.Kit
import de.royzer.fabrichg.kit.cooldown.hasCooldown
import de.royzer.fabrichg.kit.cooldown.sendCooldown
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponents
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.silkmc.silk.core.item.setCustomName
import net.silkmc.silk.core.item.setLore
import net.silkmc.silk.core.text.literalText
import kotlin.reflect.KProperty

fun kitItem(itemStack: ItemStack, kit: Kit): KitItem {
    return KitItem(itemStack, kit)
}


class KitItem(
    var itemStack: ItemStack,
    val kit: Kit,
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
    private val kitItemStack: ItemStack get() = itemStack.apply {
        setLore(listOf(literalText("Kititem")))
        if (!hasCustomHoverName()) {
            setCustomName(kit.name)
        }
    }.copy()

    // prüft auf cooldown und ruft ggf. die übergebene action auf (meistens das invoken der entsprechenden action) + sendet ggf. cooldown msg
    fun invokeKitItemAction(
        hgPlayer: HGPlayer,
        kit: Kit,
        sendCooldown: Boolean = true,
        ignoreCooldown: Boolean = false,
        action: () -> Unit
    ) {
        if (hgPlayer.canUseKit(kit, ignoreCooldown)) {
            action.invoke()
        } else if (hgPlayer.hasCooldown(kit)) {
            if (sendCooldown) {
                hgPlayer.serverPlayer?.sendCooldown(kit)
            }
        }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): ItemStack {
        return kitItemStack
    }
}

// hehe
val ItemStack.isKitItem: Boolean
    get() {
        val lore = this.get(DataComponents.LORE).toString() // TODO
        return lore.contains("Kititem")
    }

fun ItemStack.isKitItemOf(kit: Kit): Boolean {
    if (!isKitItem) return false

    val customData = get(DataComponents.CUSTOM_DATA) ?: return false

    return customData.copyTag().getString("kit") == kit.name
}