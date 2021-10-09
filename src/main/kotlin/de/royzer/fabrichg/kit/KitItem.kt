package de.royzer.fabrichg.kit

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.cooldown.hasCooldown
import de.royzer.fabrichg.kit.cooldown.sendCooldown
import de.royzer.fabrichg.sendPlayerStatus
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

class KitItem(
    var itemStack: ItemStack,
    var droppable: Boolean = false,
    internal var clickAtEntityAction: ((HGPlayer, Kit, Entity, Hand) -> Unit)? = null,
    internal var clickAtPlayerAction: ((HGPlayer, Kit, ServerPlayerEntity, Hand) -> Unit)? = null,
    internal var placeAction: ((HGPlayer, Kit, ItemStack, BlockPos, World) -> Unit)? = null,
    internal var clickAction: ((HGPlayer, Kit) -> Unit)? = null,
    internal var useOnBlockAction: ((HGPlayer, Kit, ItemUsageContext) -> Unit)? = null
) {
    fun invokeUseOnBlockAction(hgPlayer: HGPlayer, kit: Kit, context: ItemUsageContext, ignoreCooldown: Boolean = false){
        if (hgPlayer.canUseKit(kit, ignoreCooldown)) {
            useOnBlockAction?.invoke(hgPlayer, kit, context)
        } else if (hgPlayer.hasCooldown(kit)) {
            hgPlayer.serverPlayerEntity?.sendCooldown(kit)
        }
    }

    fun invokeClickAction(hgPlayer: HGPlayer, kit: Kit, ignoreCooldown: Boolean = false) {
        if (hgPlayer.canUseKit(kit, ignoreCooldown)) {
            clickAction?.invoke(hgPlayer, kit)
        } else if (hgPlayer.hasCooldown(kit)) {
            hgPlayer.serverPlayerEntity?.sendCooldown(kit)
        }
    }

    fun invokePlaceAction(hgPlayer: HGPlayer, kit: Kit, itemStack: ItemStack, blockPos: BlockPos, world: World, ignoreCooldown: Boolean = false) {
        if (hgPlayer.canUseKit(kit, ignoreCooldown)) {
            placeAction?.invoke(hgPlayer, kit, itemStack, blockPos, world)
        } else if (hgPlayer.hasCooldown(kit)) {
            hgPlayer.serverPlayerEntity?.sendCooldown(kit)
        }
    }

    fun invokeClickAtEntityAction(hgPlayer: HGPlayer, kit: Kit, entity: Entity, hand: Hand, ignoreCooldown: Boolean = false) {
        if (hgPlayer.canUseKit(kit, ignoreCooldown)) {
            if (entity is ServerPlayerEntity)
                clickAtPlayerAction?.invoke(hgPlayer, kit, entity, hand)
            else clickAtEntityAction?.invoke(hgPlayer, kit, entity, hand)
        } else if (hgPlayer.hasCooldown(kit)) {
            hgPlayer.serverPlayerEntity?.sendCooldown(kit)
        }
    }
}

fun kitItem(itemStack: ItemStack): KitItem {
    return KitItem(itemStack)
}

fun onUseBlock(player: PlayerEntity, context: ItemUsageContext ){
    val serverPlayerEntity = player as? ServerPlayerEntity ?: return
    val hgPlayer = serverPlayerEntity.hgPlayer
    if(context.stack.isKitItem){
        hgPlayer.kits.forEach { kit ->
            kit.kitItems.forEach {
                if(it.itemStack.item == context.stack.item){
                    it.invokeUseOnBlockAction(hgPlayer, kit, context)
                }
            }
        }
    }
}

@JvmOverloads
fun onClick(player: PlayerEntity, itemStack: ItemStack, cir: CallbackInfoReturnable<TypedActionResult<ItemStack>>? = null) {
    val serverPlayerEntity = player as? ServerPlayerEntity ?: return
    val hgPlayer = serverPlayerEntity.hgPlayer
    if (itemStack.isKitItem)
        hgPlayer.kits.forEach { kit ->
            kit.kitItems.forEach {
                if (it.itemStack.item == itemStack.item)
                    it.invokeClickAction(hgPlayer, kit)
            }
        }
}

fun onClickAtEntity(player: PlayerEntity, hand: Hand, entity: Entity, cir: CallbackInfoReturnable<ActionResult>) {
    val serverPlayerEntity = player as? ServerPlayerEntity ?: return
    val hgPlayer = serverPlayerEntity.hgPlayer
    val mainHandStack = serverPlayerEntity.mainHandStack
    val offhandStack = serverPlayerEntity.offHandStack
    if (mainHandStack.isKitItem || offhandStack.isKitItem)
        hgPlayer.kits.forEach { kit ->
            kit.kitItems.forEach {
                if (it.itemStack.item == mainHandStack.item || offhandStack.item == it.itemStack.item)
                    it.invokeClickAtEntityAction(hgPlayer, kit, entity, hand)
            }
        }
}

fun onPlace(context: ItemPlacementContext, cir: CallbackInfoReturnable<ActionResult>) {
    if (context.stack.isKitItem) {
        val serverPlayerEntity = (context.player as? ServerPlayerEntity) ?: return
        cir.returnValue = ActionResult.PASS
        serverPlayerEntity.hgPlayer.kits.forEach { kit ->
            kit.kitItems.filter { it.itemStack.item == context.stack.item }.forEach {
                it.invokeClickAction(serverPlayerEntity.hgPlayer, kit)
                it.invokePlaceAction(serverPlayerEntity.hgPlayer, kit, context.stack, context.blockPos, context.world)
            }
        }
        serverPlayerEntity.sendPlayerStatus()
    }
}

// hehe
val ItemStack.isKitItem: Boolean get() {
    val lore = getSubNbt("display")?.get("Lore")?.asString() ?: return false
    return lore.contains("Kititem")
}