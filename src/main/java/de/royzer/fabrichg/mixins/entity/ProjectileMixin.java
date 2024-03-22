package de.royzer.fabrichg.mixins.entity;

import de.royzer.fabrichg.kit.events.kit.invoker.OnProjectileHitKt;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(Projectile.class)
public class ProjectileMixin {

    @Redirect(
            method = "onHit",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/projectile/Projectile;onHitEntity(Lnet/minecraft/world/phys/EntityHitResult;)V"
            )
    )
    public void onHit(Projectile instance, EntityHitResult entityHitResult) {
        OnProjectileHitKt.onProjectileHit(entityHitResult, instance);
    }
}
