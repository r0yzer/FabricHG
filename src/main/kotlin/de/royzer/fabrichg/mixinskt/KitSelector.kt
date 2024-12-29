package de.royzer.fabrichg.mixinskt

import de.royzer.fabrichg.gui.gameSettins.gameSettingsGUI
import de.royzer.fabrichg.gui.kitSelectorGUI
import kotlinx.coroutines.runBlocking
import net.silkmc.silk.igui.openGui
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
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
        var index = 0
        val displayName = stack.displayName.string
        if (displayName.startsWith("[Kit Selector")) {
            val indexStr = displayName.split(" ").getOrNull(2)?.removeSuffix("]")
            index = if (indexStr == null) 1 else indexStr.toIntOrNull() ?: 1
        }
        val player = playerEntity as? ServerPlayer ?: return
        player.playNotifySound(SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 10f, 1f)
        player.openGui(kitSelectorGUI(player, index), 1)
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
            runBlocking {
                player.openGui(gameSettingsGUI(player), 1)
            }
        }
    }
}
