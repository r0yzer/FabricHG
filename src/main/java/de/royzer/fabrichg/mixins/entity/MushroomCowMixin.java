package de.royzer.fabrichg.mixins.entity;

import de.royzer.fabrichg.settings.ConfigManager;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(MushroomCow.class)
public class MushroomCowMixin {
    @ModifyArg(
            method = "mobInteract",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;<init>(Lnet/minecraft/world/level/ItemLike;)V",
                    ordinal = 1
            )
    )
    public ItemLike nerfMushroomCow(ItemLike item) {
        if (ConfigManager.INSTANCE.getGameSettings().getMushroomCowNerf()) {
            return Items.BEETROOT_SOUP;
        }
        return item;
    }
}
