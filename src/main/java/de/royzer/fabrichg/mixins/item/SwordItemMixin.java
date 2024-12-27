package de.royzer.fabrichg.mixins.item;

import kotlin.random.Random;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SwordItem.class)
public abstract class SwordItemMixin extends TieredItem {
    public SwordItemMixin(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Inject(method = "postHurtEnemy", at = @At("HEAD"), cancellable = true)
    public void breakReduction(ItemStack stack, LivingEntity target, LivingEntity attacker, CallbackInfo ci) {
        double breakReduction = 0.5;

        if (Random.Default.nextDouble() <= breakReduction) {
            ci.cancel();
        }
    }
}
