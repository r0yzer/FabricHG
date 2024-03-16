package de.royzer.fabrichg.mixins.item;

import de.royzer.fabrichg.kit.events.kit.OnDrinkKt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionItem.class)
public class PotionMixin {
    @Inject(
            method = "finishUsingItem",
            at = @At("HEAD")
    )
    public void finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> cir) {
        OnDrinkKt.onDrink(stack, livingEntity);
    }
}
