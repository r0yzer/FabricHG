package de.royzer.fabrichg.mixins.kits;

import de.royzer.fabrichg.kit.kits.NeoKitKt;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Projectile.class)
public class NeoMixin {

    @Redirect(
            method = "onHit",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/projectile/Projectile;onHitEntity(Lnet/minecraft/world/phys/EntityHitResult;)V"
            )
    )
    public void onHit(Projectile instance, EntityHitResult entityHitResult) {
        NeoKitKt.neoOnProjectileHit(entityHitResult, instance);
    }
}
