package de.royzer.fabrichg.kit

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.broadcast
import de.royzer.fabrichg.kit.cooldown.cooldown
import de.royzer.fabrichg.kit.cooldown.hasCooldown
import de.royzer.fabrichg.kit.cooldown.sendCooldown
import de.royzer.fabrichg.sendPlayerStatus
import net.axay.fabrik.core.text.literalText
import net.axay.fabrik.core.text.sendText
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.TypedActionResult
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

class KitItem(
    val itemStack: ItemStack,
    val droppable: Boolean = false,
    private val clickAction: ((HGPlayer, Kit) -> Unit)? = null
) {
    fun invokeClickAction(hgPlayer: HGPlayer, kit: Kit, ignoreCooldown: Boolean = false) {
        if (hgPlayer.canUseKit(kit, ignoreCooldown)) {
            clickAction?.invoke(hgPlayer, kit)
        } else if (hgPlayer.hasCooldown(kit)) {
            hgPlayer.serverPlayerEntity?.sendCooldown(kit)
        }
    }
}

fun onClick(player: PlayerEntity, itemStack: ItemStack, cir: CallbackInfoReturnable<TypedActionResult<ItemStack>>) {
    val serverPlayerEntity = player as? ServerPlayerEntity ?: return
    val hgPlayer = serverPlayerEntity.hgPlayer
    if (itemStack.isKitItem)
        hgPlayer.kits.forEach { kit ->
            kit.kitItems.forEach {
               it.invokeClickAction(hgPlayer, kit)
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
            }
        }
        serverPlayerEntity.sendPlayerStatus()
    }
}

// hehe
val ItemStack.isKitItem: Boolean get() {
    val lore = getOrCreateSubNbt("display").get("Lore")?.asString() ?: return false
    return lore.contains("Kititem")
}