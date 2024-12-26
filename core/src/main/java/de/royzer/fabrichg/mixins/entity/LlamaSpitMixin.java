package de.royzer.fabrichg.mixins.entity;

import de.royzer.fabrichg.kit.kits.SpitKitKt;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LlamaSpit.class)
public class LlamaSpitMixin {
    @Inject(
            method = "onHitEntity",
            at = @At("HEAD")
    )
    public void onHit(EntityHitResult result, CallbackInfo ci) {
        SpitKitKt.onSpitHit(result, (LlamaSpit) (Object) this);
    }
}
