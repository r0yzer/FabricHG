package de.royzer.fabrichg.kit.events.kit.invoker

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.events.kit.invokeKitAction
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.CraftingRecipe
import net.minecraft.world.item.crafting.RecipeHolder

fun onCraft(player: Player, stack: ItemStack, recipe: RecipeHolder<CraftingRecipe>) {
    if (player !is ServerPlayer) return

    val hgPlayer = player.hgPlayer
    hgPlayer.kits.forEach { kit ->
        hgPlayer.invokeKitAction(kit, sendCooldown = true) {
            kit.events.craftAction?.invoke(hgPlayer, stack, recipe, kit)
        }
    }
}