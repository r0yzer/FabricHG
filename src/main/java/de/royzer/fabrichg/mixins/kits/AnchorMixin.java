package de.royzer.fabrichg.mixins.kits;

import de.royzer.fabrichg.kit.kits.AnchorKitKt;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
class AnchorLivingEntityMixin {
    @Inject(
            method = "knockback",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onKnock(double strength, double x, double z, CallbackInfo ci) {
        AnchorKitKt.onAnchorAttack(strength, x, z, ci, (LivingEntity) (Object) this);
    }
}

