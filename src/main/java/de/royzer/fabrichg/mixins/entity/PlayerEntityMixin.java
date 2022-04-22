package de.royzer.fabrichg.mixins.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Player.class)
public class PlayerEntityMixin {
    @Redirect(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V"
            )
    )
    public void changeKnockback(LivingEntity instance, double strength, double x, double z) {
        System.out.println("mejrel");
        Player playerEntity = (Player) (Object) (this);
        int j = EnchantmentHelper.getKnockbackBonus((playerEntity));
        instance.knockback(0.4000000059604645D, Math.sin(playerEntity.getYRot() * 0.017453292F), -Math.cos(playerEntity.getXRot() * 0.017453292F));
    }
}
