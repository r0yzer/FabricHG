package de.royzer.fabrichg.mixins.kits;

import de.royzer.fabrichg.kit.kits.NeoKitKt;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Projectile.class)
public class NeoMixin {
    @Inject(
            method = "onHitEntity",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        NeoKitKt.neoOnProjectileHit(entityHitResult, (Projectile) (Object) this, ci);
    }
}
