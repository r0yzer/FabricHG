package de.royzer.fabrichg.mixins.kits;

import de.royzer.fabrichg.kit.kits.AnchorKitKt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class AnchorMixin {
    @Inject(
            method = "attack",
            at = @At("RETURN")
    )
    public void onAttackEntity(Entity target, CallbackInfo ci) {
        AnchorKitKt.onAnchorAttackEntity(target, (ServerPlayerEntity) (Object) this);
    }
//    @Inject(
//            method = "damage",
//            at = @At("RETURN")
//    )
//    public void onAttackedByEntity(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
//        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) (Object) this;
//        HGPlayer hgPlayer = PlayerList.INSTANCE.getPlayer(serverPlayerEntity.getUuid());
//        if (hgPlayer.hasKit(AnchorKit.INSTANCE)) {
//            serverPlayerEntity.setVelocity(0,0,0);
//        }
//    }
}

@Mixin(LivingEntity.class)
class AnchorLivingEntityMixin {
    @Inject(
            method = "takeKnockback",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onKnock(double strength, double x, double z, CallbackInfo ci) {
        AnchorKitKt.onAnchorKnockback(strength, x, z, ci, (LivingEntity) (Object) this);
    }
}

