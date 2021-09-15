package de.royzer.fabrichg.mixins.kits;

import de.royzer.fabrichg.kit.kits.NeoKitKt;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProjectileEntity.class)
public class NeoMixin {
    @Inject(
            method = "onEntityHit",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        NeoKitKt.neoOnProjectileHit(entityHitResult, (ProjectileEntity) (Object) this, ci);
    }
}
