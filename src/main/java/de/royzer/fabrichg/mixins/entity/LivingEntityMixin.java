package de.royzer.fabrichg.mixins.entity;

import de.royzer.fabrichg.mixinskt.LivingEntityMixinKt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(
            method = "hurt",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntityMixinKt.INSTANCE.onDamage(source, amount, (LivingEntity) (Object) this, cir);
    }
}
