package de.royzer.fabrichg.mixins.server.network;

import com.mojang.authlib.GameProfile;
import de.royzer.fabrichg.kit.events.KitEventsKt;
import de.royzer.fabrichg.mixinskt.ServerPlayerEntityMixinKt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Inject(
            method = "damage",
            at = @At("HEAD")
    )
    public void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayerEntityMixinKt.INSTANCE.onDamage(source, amount, cir, (ServerPlayerEntity) (Object) (this));
    }
    @Redirect(
            method = "damage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
            )
    )
    public boolean reduceDamage(PlayerEntity instance, DamageSource source, float amount) {
        if (source.getAttacker() instanceof ServerPlayerEntity) {
            return super.damage(source, (float) (amount * 0.6));
        } else {
            return super.damage(source, amount);
        }
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
