package de.royzer.fabrichg.mixins.entity;

import de.royzer.fabrichg.kit.events.kit.invoker.OnProjectileHitKt;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Projectile.class)
public class ProjectileMixin {


    @Inject(
            method = "onHit",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/projectile/Projectile;onHitEntity(Lnet/minecraft/world/phys/EntityHitResult;)V",
            shift = At.Shift.AFTER)
    )
    public void onHit(HitResult result, CallbackInfo ci) {
        OnProjectileHitKt.onProjectileHit((EntityHitResult) result, (Projectile) (Object) this);
    }

}

