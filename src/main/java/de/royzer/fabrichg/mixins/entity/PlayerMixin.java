package de.royzer.fabrichg.mixins.entity;

import de.royzer.fabrichg.game.GamePhaseManager;
import de.royzer.fabrichg.game.phase.PhaseType;
import de.royzer.fabrichg.kit.events.kit.invoker.OnAttackEntityKt;
import de.royzer.fabrichg.kit.events.kit.invoker.OnSneakKt;
import de.royzer.fabrichg.settings.ConfigManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    @Shadow public abstract float getAttackStrengthScale(float adjustTicks);

    @Shadow protected abstract float getEnchantedDamage(Entity entity, float damage, DamageSource damageSource);

    @Unique
    private Entity brianBusting;

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(
            method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getKnockback(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;)F"),
            cancellable = true
    )
    public void afterDamageEntity(Entity target, CallbackInfo ci) {
        OnAttackEntityKt.afterDamageEntity(target, this, ci);
    }

    @Inject(
            method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"),
            cancellable = true
    )
    public void onAttackEntity(Entity target, CallbackInfo ci) {
        OnAttackEntityKt.onAttackEntity(target, this, ci);
    }


    @Redirect(
            method = "updatePlayerPose",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;setPose(Lnet/minecraft/world/entity/Pose;)V")
    )
    void abc(Player instance, Pose pose) {
        if (instance.getPose() != Pose.CROUCHING && pose == Pose.CROUCHING) {
            OnSneakKt.onSneak(instance, pose);
        }
        instance.setPose(pose);
    }

    @Inject(
            method = "attack",
            at = @At("HEAD")
    )
    private void helm(Entity target, CallbackInfo ci) {
        this.brianBusting = target; // Store the target entity
    }

    @ModifyArgs(
            method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z")
    )
    public void brainBusting(Args args) {
        // ASKLFDJLFDJLFDjjefdooooooodsfgahjkvhhhhh werD OKSALftr ioASD JSJ agfio FSADF

        float f1 = this.isAutoSpinAttack() ? this.autoSpinAttackDmg : (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        DamageSource damageSource = this.damageSources().playerAttack((Player) (Object) this);
        float g1 = this.getEnchantedDamage(brianBusting, f1, damageSource) - f1;
        float h1 = this.getAttackStrengthScale(0.5F);
        float f2 = f1 * (0.2F + h1 * h1 * 0.8F);
        float g2 = g1 * h1;
        float originalDamage = f2 + g2;
        float damageMultiplier = ConfigManager.INSTANCE.getGameSettings().getCritDamage();

        float givenDamage = args.get(1);

        boolean isCrit = givenDamage != originalDamage;

        if (isCrit) {
            float newDamage = originalDamage * damageMultiplier;
            args.set(1, newDamage);
        }
    }

    @Inject(method = "interactOn", at = @At("HEAD"), cancellable = true)
    public void cancelChestInteraction(Entity entityToInteractOn, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        // geht nicht kp
        if (GamePhaseManager.INSTANCE.getCurrentPhaseType() == PhaseType.LOBBY) {
            cir.setReturnValue(InteractionResult.PASS);
            cir.cancel();
        }
    }
}
