package de.royzer.fabrichg.mixinskt

import de.royzer.fabrichg.gui.kitSelectorGUI
import net.axay.fabrik.core.logging.logInfo
import net.axay.fabrik.igui.openGui
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

object KitSelector {
    fun onClick(playerEntity: PlayerEntity, stack: ItemStack, cir: CallbackInfoReturnable<TypedActionResult<ItemStack>>, world: World, hand: Hand) {
        if (stack.name.string == "Kit Selector") {
            val player = playerEntity as? ServerPlayerEntity ?: return
            player.openGui(kitSelectorGUI(player), 1)
        }
    }
}