package de.royzer.fabrichg.mixins.server.network;

import com.mojang.authlib.GameProfile;
import de.royzer.fabrichg.data.hgplayer.HGPlayer;
import de.royzer.fabrichg.data.hgplayer.HGPlayerKt;
import de.royzer.fabrichg.game.PlayerList;
import de.royzer.fabrichg.gulag.GulagManager;
import de.royzer.fabrichg.kit.events.kit.invoker.OnAttackEntityKt;
import de.royzer.fabrichg.kit.events.kit.invoker.OnLeftClickKt;
import de.royzer.fabrichg.kit.events.kit.invoker.OnTakeDamageKt;
import de.royzer.fabrichg.kit.events.kit.invoker.OnTickKt;
import de.royzer.fabrichg.mixinskt.LivingEntityMixinKt;
import de.royzer.fabrichg.mixinskt.ServerPlayerEntityMixinKt;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
//    @Shadow protected abstract void fudgeSpawnLocation(ServerLevel serverLevel);

    public ServerPlayerMixin(Level world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Inject(
            method = "hurt",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayerEntityMixinKt.INSTANCE.onDamage(source, amount, cir, (ServerPlayer) (Object) (this));

        if (!LivingEntityMixinKt.INSTANCE.canDamage(source, this)) return;

        boolean cancel = false;

        if ((getHealth() - amount) <= 0) {
            cancel = beforeDeath(source, amount, cir);
        }

        if (cancel) cir.cancel();
    }


    @Unique
    public boolean beforeDeath(DamageSource source, float amount, CallbackInfoReturnable<Boolean> ci) {
        boolean cancelDeath = GulagManager.INSTANCE.beforeDeath(source.getEntity(), (ServerPlayer) (Object) this);

        if (cancelDeath) {
            Entity killer = source.getEntity();
            if (killer instanceof ServerPlayer killerPlayer) {
                HGPlayer hgPlayer = HGPlayerKt.getHgPlayer(killerPlayer);
                HGPlayerKt.gulagKill(hgPlayer, (ServerPlayer) (Object) this);
                PlayerList.INSTANCE.announcePlayerDeath(hgPlayer, source, source.getEntity(), true);
            }
            setHealth(getMaxHealth());
        }

        return cancelDeath;
    }

    @Inject(
            method = "drop(Z)Z",
            at = @At(value = "HEAD", ordinal = 0),
            cancellable = true
    )
    public void onDropSelectedItem(boolean dropStack, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayerEntityMixinKt.INSTANCE.onDropSelectedItem(dropStack, cir, (ServerPlayer) (Object) this);
    }

    @Inject(
            method = "die",
            at = @At("HEAD")
    )
    public void afterDeath(DamageSource damageSource, CallbackInfo ci) {
        GulagManager.INSTANCE.afterDeath(damageSource.getEntity(), (ServerPlayer) (Object) this);
    }

    @Redirect(
            method = "hurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
            )
    )
    public boolean reduceDamage(Player instance, DamageSource source, float amount) {
        float damageAmount = OnTakeDamageKt.onTakeDamage((ServerPlayer) instance, source, amount);
        if (source.getEntity() instanceof ServerPlayer) {
            double multiplier = 0.6;
            if (((ServerPlayer) source.getEntity()).getMainHandItem().getDisplayName().getString().toLowerCase().contains("axe")) {
                multiplier = 0.3;
            }
            float damage = (float) (damageAmount * multiplier);
            return super.hurt(source, damage);
        } else {
            return super.hurt(source, damageAmount);
        }
    }

    @Inject(
            method = "attack",
            at = @At("HEAD")
    )
    public void onAttackEntity(Entity target, CallbackInfo ci) {
        OnAttackEntityKt.onAttackEntity(target, this);
    }


    @Inject(
            method = "swing",
            at = @At("HEAD")
    )
    public void onSwing(InteractionHand hand, CallbackInfo ci) {
        OnLeftClickKt.onLeftClick((ServerPlayer) (Object) this, hand);
    }

    @Inject(
            method = "attack",
            at = @At("TAIL")
    )
    public void afterAttackEntity(Entity target, CallbackInfo ci) {
        OnAttackEntityKt.afterAttackEntity(target, this);
    }

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    public void onTick(CallbackInfo ci) {
        OnTickKt.onTick((ServerPlayer) (Object) this);
    }

    @Inject(
            method = "die",
            at = @At("HEAD"),
            cancellable = true
    )
    public void stopDeath(DamageSource damageSource, CallbackInfo ci) {
        ci.cancel();
    }
}
