package de.royzer.fabrichg.mixins.kits;

import de.royzer.fabrichg.kit.kits.SwitcherKitKt;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SnowballEntity.class)
public class SwitcherMixin {
    @Inject(
            method = "onEntityHit",
            at = @At("HEAD")
    )
    public void onHitEntity(EntityHitResult entityHitResult, CallbackInfo ci) {
        SwitcherKitKt.switcherOnEntityHit(entityHitResult, ci, (SnowballEntity) (Object) this);
    }
}
