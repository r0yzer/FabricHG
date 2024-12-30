package de.royzer.fabrichg.mixins.world;

import de.royzer.fabrichg.kit.events.kit.invoker.OnCraftKt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ResultSlot.class)
public class ResultSlotMixin {
    @Shadow @Final private CraftingContainer craftSlots;

    @Shadow private int removeCount;

    @Inject(method = "onTake", at = @At("HEAD"))
    public void onTake(Player player, ItemStack stack, CallbackInfo ci) {
        Level playerLevel = player.level();
        RecipeManager recipeManager = playerLevel.getRecipeManager();
        CraftingInput.Positioned positionedCraftingInput = this.craftSlots.asPositionedCraftInput();
        CraftingInput craftingInput = positionedCraftingInput.input();

        Optional<RecipeHolder<CraftingRecipe>> craftedRecipe = recipeManager.getRecipeFor(RecipeType.CRAFTING, craftingInput, player.level());
        if (craftedRecipe.isEmpty()) return;

        RecipeHolder<CraftingRecipe> recipe = craftedRecipe.get();

        ItemStack assebledStack = stack;

        if (stack.getItem() == Items.AIR) {
            try {
                assebledStack = recipe.value().assemble(craftingInput, null);
            } catch (Exception e) {
            }
        }

        OnCraftKt.onCraft(player, assebledStack, recipe);
    }
}
