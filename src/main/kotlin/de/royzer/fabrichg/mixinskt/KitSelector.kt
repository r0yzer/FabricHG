package de.royzer.fabrichg.mixinskt

import de.royzer.fabrichg.gui.gameSettingsGUI
import de.royzer.fabrichg.gui.kitSelectorGUI
import net.silkmc.silk.igui.openGui
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

object KitSelector {
    fun onClick(
        playerEntity: Player,
        stack: ItemStack,
        cir: CallbackInfoReturnable<InteractionResultHolder<ItemStack>>,
        world: Level,
        hand: InteractionHand
    ) {
        if (stack.displayName.string == "[Kit Selector]") {
            val player = playerEntity as? ServerPlayer ?: return
            player.openGui(kitSelectorGUI(player), 1)
        }
    }

    fun onClickCompatator(
        playerEntity: Player,
        stack: ItemStack,
        cir: CallbackInfoReturnable<InteractionResultHolder<ItemStack>>,
        world: Level,
        hand: InteractionHand
    ) {
        if (stack.displayName.string == "[Game settings]") {
            val player = playerEntity as? ServerPlayer ?: return
            player.openGui(gameSettingsGUI(player), 1)
        }
    }
}
