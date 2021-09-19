package de.royzer.fabrichg.mixins.entity;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Redirect(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;takeKnockback(DDD)V"
            )
    )
    public void changeKnockback(LivingEntity livingEntity, double strength, double x, double z) {
        System.out.println("mejrel");
        PlayerEntity playerEntity = (PlayerEntity) (Object) (this);
        int j = EnchantmentHelper.getKnockback((playerEntity));
        livingEntity.takeKnockback(0.4000000059604645D, MathHelper.sin(playerEntity.getYaw() * 0.017453292F), -MathHelper.cos(playerEntity.getYaw() * 0.017453292F));
    }
}
