package de.royzer.fabrichg.mixins.server.network;

import de.royzer.fabrichg.kit.events.KitEventsKt;
import de.royzer.fabrichg.kit.kits.MagmaKitKt;
import de.royzer.fabrichg.mixinskt.ServerPlayerEntityMixinKt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(
            method = "damage",
            at = @At("HEAD")
    )
    public void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayerEntityMixinKt.INSTANCE.onDamage(source, amount, cir, (ServerPlayerEntity) (Object) (this));
    }
    @Inject(
            method = "dropSelectedItem",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onDropSelectedItem(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayerEntityMixinKt.INSTANCE.onDropSelectedItem(entireStack, cir, (ServerPlayerEntity) (Object) this);
    }
    @Inject(
            method = "attack",
            at = @At("HEAD")
    )
    public void onAttackPlayer(Entity target, CallbackInfo ci) {
        KitEventsKt.onAttackEntity(target, (ServerPlayerEntity) (Object) this);
    }
}
