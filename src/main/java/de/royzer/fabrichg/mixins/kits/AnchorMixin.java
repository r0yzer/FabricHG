package de.royzer.fabrichg.mixins.kits;

import de.royzer.fabrichg.data.hgplayer.HGPlayer;
import de.royzer.fabrichg.game.PlayerList;
import de.royzer.fabrichg.kit.kits.AnchorKit;
import de.royzer.fabrichg.kit.kits.MagmaKit;
import net.axay.fabrik.core.math.vector.VectorExtensionsKt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public class AnchorMixin {
    @Inject(
            method = "attack",
            at = @At("RETURN")
    )
    public void onAttackEntity(Entity target, CallbackInfo ci) {
        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) (Object) this;
        HGPlayer hgPlayer = PlayerList.INSTANCE.getPlayer(serverPlayerEntity.getUuid());
        if (hgPlayer.hasKit(AnchorKit.INSTANCE)) {
            VectorExtensionsKt.modifyVelocity(target, 0,0,0, false);
        }
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
        //noinspection ConstantConditions
        if ((LivingEntity) (Object) this instanceof ServerPlayerEntity player) {
            HGPlayer hgPlayer = PlayerList.INSTANCE.getPlayer(player.getUuid());
            if (hgPlayer == null) return;
            if (hgPlayer.hasKit(AnchorKit.INSTANCE)) {
                ci.cancel();
            }
        }
    }
}

