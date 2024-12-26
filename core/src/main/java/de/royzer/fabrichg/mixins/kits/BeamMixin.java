package de.royzer.fabrichg.mixins.kits;

import de.royzer.fabrichg.kit.kits.BeamKitKt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndCrystal.class)
public class BeamMixin {
    @Inject(
            method = "hurt",
            at = @At("HEAD"),
            cancellable = true)
    public void onHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (BeamKitKt.getCrystals().contains((EndCrystal) (Object) this)) {
            cir.setReturnValue(false);
        }
    }
}
