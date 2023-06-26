package de.royzer.fabrichg.mixins.kits;

import de.royzer.fabrichg.kit.kits.AnchorKitKt;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class AnchorMixin {
//    @Inject(
//            method = "attack",
//            at = @At("HEAD"),
//            cancellable = true
//    )
//    public void onAttackEntity(Entity target, CallbackInfo ci) {
//        boolean b = AnchorKitKt.onAnchorAttackEntity(target, ci, (ServerPlayer) (Object) this);
//        if (b) {
//
//        }
//    }
}

@Mixin(Player.class)
class AnchorPlayerMixin {
//    @Redirect(
//            method = "attack",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V"
//            )
//    )
//    public void onAttackentity(LivingEntity target, double d, double e, double f) {
//        ServerPlayer serverPlayer = (ServerPlayer) (Object) this;
//        boolean b = AnchorKitKt.onAnchorAttackEntity2(serverPlayer, target);
//    }
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

