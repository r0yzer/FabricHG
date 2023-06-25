package de.royzer.fabrichg.kit

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.cooldown.hasCooldown
import de.royzer.fabrichg.kit.cooldown.sendCooldown
import de.royzer.fabrichg.sendPlayerStatus
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

class KitItem(
    var itemStack: ItemStack,
    var droppable: Boolean = false,
    internal var clickAtEntityAction: ((HGPlayer, Kit, Entity, InteractionHand) -> Unit)? = null,
    internal var clickAtPlayerAction: ((HGPlayer, Kit, ServerPlayer, InteractionHand) -> Unit)? = null,
    internal var placeAction: ((HGPlayer, Kit, ItemStack, BlockPos, Level) -> Unit)? = null,
    internal var clickAction: ((HGPlayer, Kit) -> Unit)? = null,
    internal var useOnBlockAction: ((HGPlayer, Kit, BlockPlaceContext) -> Unit)? = null
) {
    fun invokeUseOnBlockAction(hgPlayer: HGPlayer, kit: Kit, context: BlockPlaceContext, ignoreCooldown: Boolean = false){
        if (hgPlayer.canUseKit(kit, ignoreCooldown)) {
            useOnBlockAction?.invoke(hgPlayer, kit, context)
        } else if (hgPlayer.hasCooldown(kit)) {
            hgPlayer.serverPlayer?.sendCooldown(kit)
        }
    }

    fun invokeClickAction(hgPlayer: HGPlayer, kit: Kit, ignoreCooldown: Boolean = false) {
        if (hgPlayer.canUseKit(kit, ignoreCooldown)) {
            clickAction?.invoke(hgPlayer, kit)
        } else if (hgPlayer.hasCooldown(kit)) {
            hgPlayer.serverPlayer?.sendCooldown(kit)
        }
    }

    fun invokePlaceAction(hgPlayer: HGPlayer, kit: Kit, itemStack: ItemStack, blockPos: BlockPos, world: Level, ignoreCooldown: Boolean = false) {
        if (hgPlayer.canUseKit(kit, ignoreCooldown)) {
            placeAction?.invoke(hgPlayer, kit, itemStack, blockPos, world)
        } else if (hgPlayer.hasCooldown(kit)) {
            hgPlayer.serverPlayer?.sendCooldown(kit)
        }
    }

    fun invokeClickAtEntityAction(hgPlayer: HGPlayer, kit: Kit, entity: Entity, hand: InteractionHand, ignoreCooldown: Boolean = false) {
        if (hgPlayer.canUseKit(kit, ignoreCooldown)) {
            if (entity is ServerPlayer)
                clickAtPlayerAction?.invoke(hgPlayer, kit, entity, hand)
            else clickAtEntityAction?.invoke(hgPlayer, kit, entity, hand)
        } else if (hgPlayer.hasCooldown(kit)) {
            hgPlayer.serverPlayer?.sendCooldown(kit)
        }
    }
}

fun kitItem(itemStack: ItemStack): KitItem {
    return KitItem(itemStack)
}

fun onUseBlock(player: Player, context: BlockPlaceContext ){
    val serverPlayerEntity = player as? ServerPlayer ?: return
    val hgPlayer = serverPlayerEntity.hgPlayer
    if(context.itemInHand.isKitItem){
        hgPlayer.kits.forEach { kit ->
            kit.kitItems.forEach {
                if(it.itemStack.item == context.itemInHand.item){
                    it.invokeUseOnBlockAction(hgPlayer, kit, context)
                }
            }
        }
    }
}

@JvmOverloads
fun onClick(player: Player, itemStack: ItemStack, cir: CallbackInfoReturnable<InteractionResultHolder<ItemStack>>? = null) {
    val serverPlayerEntity = player as? ServerPlayer ?: return
    val hgPlayer = serverPlayerEntity.hgPlayer
    if (itemStack.isKitItem)
        hgPlayer.kits.forEach { kit ->
            kit.kitItems.forEach {
                if (it.itemStack.item == itemStack.item)
                    it.invokeClickAction(hgPlayer, kit)
            }
        }
}

fun onClickAtEntity(player: Player, hand: InteractionHand, entity: Entity, cir: CallbackInfoReturnable<InteractionResult>) {
    val serverPlayerEntity = player as? ServerPlayer ?: return
    val hgPlayer = serverPlayerEntity.hgPlayer
    val mainHandStack = serverPlayerEntity.mainHandItem
    val offhandStack = serverPlayerEntity.offhandItem
    if (mainHandStack.isKitItem || offhandStack.isKitItem)
        hgPlayer.kits.forEach { kit ->
            kit.kitItems.forEach {
                if (it.itemStack.item == mainHandStack.item || offhandStack.item == it.itemStack.item)
                    it.invokeClickAtEntityAction(hgPlayer, kit, entity, hand)
            }
        }
}

fun onPlace(context: BlockPlaceContext, cir: CallbackInfoReturnable<InteractionResult>) {
    if (context.itemInHand.isKitItem) {
        val serverPlayerEntity = (context.player as? ServerPlayer) ?: return
        cir.returnValue = InteractionResult.PASS
        serverPlayerEntity.hgPlayer.kits.forEach { kit ->
            kit.kitItems.filter { it.itemStack.item == context.itemInHand.item }.forEach {
                it.invokeClickAction(serverPlayerEntity.hgPlayer, kit)
                it.invokePlaceAction(serverPlayerEntity.hgPlayer, kit, context.itemInHand, context.clickedPos, context.level)
            }
        }
        serverPlayerEntity.sendPlayerStatus()
    }
}

// hehe
val ItemStack.isKitItem: Boolean get() {
    val lore = getTagElement("display").toString()
    return lore.contains("Kititem")
}