package de.royzer.fabrichg.kit

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.broadcast
import de.royzer.fabrichg.kit.cooldown.cooldown
import de.royzer.fabrichg.kit.cooldown.sendCooldown
import net.axay.fabrik.core.text.literalText
import net.axay.fabrik.core.text.sendText
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.TypedActionResult
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

class KitItem(
    val itemStack: ItemStack,
    val droppable: Boolean = false,
    val clickAction: ((HGPlayer, Kit) -> Unit)? = null
)

fun onClick(player: PlayerEntity, itemStack: ItemStack, cir: CallbackInfoReturnable<TypedActionResult<ItemStack>>) {
    val serverPlayerEntity = player as? ServerPlayerEntity ?: return
    val hgPlayer = serverPlayerEntity.hgPlayer
    hgPlayer.kits.forEach { kit ->
        kit.kitItems.forEach {
            if (itemStack.isKitItem)
                if (hgPlayer.canUseKit(kit))
                    it.clickAction?.invoke(hgPlayer, kit)
                else
                    serverPlayerEntity.sendCooldown(kit)
        }
    }
}

// hehe
val ItemStack.isKitItem: Boolean get() {
    val lore = getOrCreateSubNbt("display").get("Lore")?.asString() ?: return false
    return lore.contains("Kititem")
}