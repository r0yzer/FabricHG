package de.royzer.fabrichg.mixins.kits;

import de.royzer.fabrichg.kit.kits.AnchorKitKt;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class AnchorMixin {
    @Inject(
            method = "attack",
            at = @At("RETURN")
    )
    public void onAttackEntity(Entity target, CallbackInfo ci) {
        AnchorKitKt.onAnchorAttackEntity(target, (ServerPlayer) (Object) this);
    }
}

@Mixin(LivingEntity.class)
class AnchorLivingEntityMixin {
    @Inject(
            method = "knockback",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onKnock(double strength, double x, double z, CallbackInfo ci) {
        AnchorKitKt.onAnchorKnockback(strength, x, z, ci, (LivingEntity) (Object) this);
    }
}

