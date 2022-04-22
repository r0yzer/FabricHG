package de.royzer.fabrichg.mixins.kits;

import de.royzer.fabrichg.kit.kits.SwitcherKitKt;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Snowball.class)
public class SwitcherMixin {
    @Inject(
            method = "onHitEntity",
            at = @At("HEAD")
    )
    public void onHitEntity(EntityHitResult entityHitResult, CallbackInfo ci) {
        SwitcherKitKt.switcherOnEntityHit(entityHitResult, ci, (Snowball) (Object) this);
    }
}
