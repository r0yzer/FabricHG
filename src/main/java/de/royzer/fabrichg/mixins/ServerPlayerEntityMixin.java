package de.royzer.fabrichg.mixins;

import de.royzer.fabrichg.mixinskt.LivingEntityMixinKt;
import de.royzer.fabrichg.mixinskt.ServerPlayerEntityMixinKt;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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
}
